package com.simplyti.service.clients.http.request;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.DecodingFullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.HttpRequestFilterHandler;
import com.simplyti.service.clients.http.request.stream.DefaultStreamedBody;
import com.simplyti.service.clients.http.request.stream.StreamedBody;
import com.simplyti.service.clients.http.sse.DefaultServerSentEvents;
import com.simplyti.service.clients.http.sse.ServerSentEvents;
import com.simplyti.service.clients.http.stream.HttpDataStream;
import com.simplyti.service.clients.http.stream.HttpInputStream;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.clients.stream.PendingRequest;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
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

public abstract class AbstractFinishableHttpRequestBuilder<T> implements BaseFinishableHttpRequestBuilder<T>, HeaderAppendableRequestBuilder<T>, FilterableRequestBuilder<T>, PendingRequest {

	private static final String HANDLER = "handler";
	
	private final ChannelProvider channelProvider;
	private final HttpMethod method;
	private final String path;
	
	private List<HttpRequestFilter> filters;
	
	private boolean checkStatus;
	private HttpHeaders headers;
	private Map<String,Object> params;

	public AbstractFinishableHttpRequestBuilder(ChannelProvider channelProvider, HttpMethod method, String path, Map<String,Object> params, HttpHeaders headers, boolean checkStatus,
			List<HttpRequestFilter> filters) {
		this.channelProvider=channelProvider;
		this.method=method;
		this.path=path;
		this.headers=headers;
		this.params=params;
		this.checkStatus=checkStatus;
		this.filters=filters;
	}

	@Override
	public Future<FullHttpResponse> fullResponse() {
		return channelProvider.channel()
				.thenCombine(channel->{
					Promise<FullHttpResponse> promise = channel.eventLoop().newPromise();
					channel.pipeline().addLast(HANDLER,new FullHttpResponseHandler(HANDLER,channel,checkStatus,promise));
					addFilterHandlerIfNecessary(channel);
					channel.writeAndFlush(request(channel)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
					return promise;
				});
	}
	
	private void addFilterHandlerIfNecessary(ClientChannel channel) {
		if(filters !=null && !filters.isEmpty()) {
			channel.pipeline().addLast(new HttpRequestFilterHandler(filters));
		}
	}

	@Override
	public <U> Future<U> fullResponse(Function<FullHttpResponse, U> fn) {
		return channelProvider.channel()
				.thenCombine(channel->{
					Promise<U> promise = channel.eventLoop().newPromise();
					channel.pipeline().addLast(HANDLER,new DecodingFullHttpResponseHandler<U>(HANDLER,channel,promise,checkStatus,fn));
					addFilterHandlerIfNecessary(channel);
					channel.writeAndFlush(request(channel)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
					return promise;
				});
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
	
	@SuppressWarnings("unchecked")
	@Override
	public T withFilter(HttpRequestFilter filter) {
		if(filters == null) {
			this.filters = new ArrayList<>();
		}
		this.filters.add(filter);
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
		return request;
	}

	protected abstract ByteBuf body(ClientChannel ch);

	@Override
	public HttpInputStream stream() {
		return new HttpDataStream(this);
	}
	
	@Override
	public ServerSentEvents sse() {
		return new DefaultServerSentEvents(this);
	}
	
	public StreamedBody withStreamBody(Consumer<ClientChannel> initializer) {
		return new DefaultStreamedBody(this,initializer);
	}
	
	@Override
	public Future<ClientChannel> channel() {
		return channelProvider.channel();
	}
	
	@Override
	public <U> Future<U> addHandlerAndSend(Future<ClientChannel> futureChannel,  Supplier<io.netty.util.concurrent.Future<U>> requestHandlerInit){
		return futureChannel.thenCombine(ch->addHandlerAndSend(ch,request(ch),__->requestHandlerInit.get()));
	}
	
	private static <U> io.netty.util.concurrent.Future<U> addHandlerAndSend(ClientChannel ch, HttpRequest request, Function<ClientChannel,io.netty.util.concurrent.Future<U>> requestHandlerInit) {
		io.netty.util.concurrent.Future<U> future = requestHandlerInit.apply(ch);
		ch.writeAndFlush(request).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		return future;
	}
	
}
