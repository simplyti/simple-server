package com.simplyti.service.clients.http.request;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.DecodingFullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.http.sse.DefaultServerSentEvents;
import com.simplyti.service.clients.http.sse.ServerSentEvents;
import com.simplyti.service.clients.http.stream.HttpDataStream;
import com.simplyti.service.clients.http.stream.HttpInputStream;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.clients.stream.PendingRequest;
import com.simplyti.service.commons.netty.Promises;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

public abstract class AbstractFinishableHttpRequestBuilder<T> implements BaseFinishableHttpRequestBuilder<T>, HeaderAppendableRequestBuilder<T>, PendingRequest {

	private static final String HANDLER = "handler";
	
	private final ChannelProvider channelProvider;
	private final HttpMethod method;
	private final String path;
	
	private boolean checkStatus;
	private HttpHeaders headers;
	private Map<String,Object> params;

	public AbstractFinishableHttpRequestBuilder(ChannelProvider channelProvider, HttpMethod method, String path, Map<String,Object> params, HttpHeaders headers, boolean checkStatus) {
		this.channelProvider=channelProvider;
		this.method=method;
		this.path=path;
		this.headers=headers;
		this.params=params;
		this.checkStatus=checkStatus;
	}

	@Override
	public Future<FullHttpResponse> fullResponse() {
		return channelProvider.channel()
				.thenCombine(ch->writeRequest(ch,request(ch)))
				.thenCombine(this::fullResponseHandler);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withHeader(String name, String value) {
		initializeHeaders();
		this.headers.set(name,value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withHeader(CharSequence name, String value) {
		initializeHeaders();
		this.headers.set(name,value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withHeader(CharSequence name, CharSequence value) {
		initializeHeaders();
		this.headers.set(name,value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withHeader(String name, CharSequence value) {
		initializeHeaders();
		this.headers.set(name,value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withBasicAuth(String user, String pass) {
		initializeHeaders();
		String userpass = user+":"+pass;
		this.headers.set(HttpHeaderNames.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes(CharsetUtil.UTF_8)));
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T params(Map<String, String> params) {
		initializeParams();
		this.params.putAll(params);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T param(String param, Object value) {
		initializeParams();
		this.params.put(param, value);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T param(String param) {
		initializeParams();
		this.params.put(param, null);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withCheckStatusCode() {
		this.checkStatus=true;
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withIgnoreStatusCode() {
		this.checkStatus=false;
		return (T) this;
	}
	
	private void initializeHeaders() {
		if(headers==null) {
			this.headers=new DefaultHttpHeaders();
		}
	}
	
	private void initializeParams() {
		if(params==null) {
			this.params=new HashMap<>();
		}
	}

	private HttpRequest request(ClientChannel ch) {
		FullHttpRequest request = buildRequest(ch);
		return setHeaders(request,ch).setUri(withParams(request.uri(),params));
	}

	protected FullHttpRequest buildRequest(ClientChannel ch) {
		ByteBuf buff = body(ch);
		FullHttpRequest request;
		if(buff!=null) {
			request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, path, buff);
		} else {
			request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, path);
		}
		return request;
	}

	private static String withParams(String uri, Map<String, Object> params) {
		if(params == null) {
			return uri;
		}
		
		QueryStringEncoder encoder = new QueryStringEncoder(uri);
		params.forEach((name,value)->encoder.addParam(name, value!=null?value.toString():null));
		return encoder.toString();
	}

	private FullHttpRequest setHeaders(FullHttpRequest request,ClientChannel ch) {
		if(headers!=null) {
			request.headers().set(headers);
		}
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
		if(!request.headers().contains(HttpHeaderNames.HOST)) {
			request.headers().set(HttpHeaderNames.HOST,ch.address().host());
		}
		return request;
	}

	protected abstract ByteBuf body(ClientChannel ch);

	private io.netty.util.concurrent.Future<FullHttpResponse> fullResponseHandler(ClientChannel channel){
		Promise<FullHttpResponse> promise = channel.eventLoop().newPromise();
		channel.pipeline().addLast(HANDLER,new FullHttpResponseHandler(HANDLER,channel,checkStatus,promise));
		return promise;
	}
	
	@Override
	public HttpInputStream stream() {
		return new HttpDataStream(this);
	}
	
	@Override
	public ServerSentEvents sse() {
		return new DefaultServerSentEvents(this);
	}
	
	@Override
	public Future<ClientChannel> send(){
		return channelProvider.channel()
				.thenCombine(ch->writeRequest(ch,request(ch)));
	}
	
	@Override
	public <U> Future<U> fullResponse(Function<FullHttpResponse, U> fn) {
		return send().thenCombine(ch->decodingFullResponseHandler(ch,fn));
	}
	
	private <U> io.netty.util.concurrent.Future<U> decodingFullResponseHandler(ClientChannel channel, Function<FullHttpResponse, U> fn){
		Promise<U> promise = channel.eventLoop().newPromise();
		channel.pipeline().addLast(HANDLER,new DecodingFullHttpResponseHandler<U>(HANDLER,channel,promise,checkStatus,fn));
		return promise;
	}
	
	public static io.netty.util.concurrent.Future<ClientChannel> writeRequest(ClientChannel ch, HttpRequest request) {
		ChannelFuture future = ch.writeAndFlush(request);
		Promise<ClientChannel> promise = ch.eventLoop().newPromise();
		Promises.ifSuccessMap(future, promise, v->ch);
		return promise;
	}

}
