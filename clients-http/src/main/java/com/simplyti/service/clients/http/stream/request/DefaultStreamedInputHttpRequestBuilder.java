package com.simplyti.service.clients.http.stream.request;

import java.util.Map;
import java.util.function.Function;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.http.sse.DefaultServerSentEvents;
import com.simplyti.service.clients.http.sse.ServerSentEvents;
import com.simplyti.service.clients.http.stream.HttpDataStream;
import com.simplyti.service.clients.http.stream.HttpInputStream;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.clients.stream.PendingRequest;
import com.simplyti.service.clients.stream.StreamedOutput;
import com.simplyti.service.commons.netty.Promises;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

public class DefaultStreamedInputHttpRequestBuilder implements StreamedInputHttpRequestBuilder, PendingRequest {

	private static final String HANDLER = "handler";
	
	private final HttpRequest request;
	private final Map<String, Object> params;
	private final ChannelProvider channelProvider;
	private final EventExecutor executor;
	private final boolean checkStatus;
	private final HttpHeaders headers;
	
	private Future<ClientChannel> futureChannel;
	private StreamedOutput streamOutput;



	public DefaultStreamedInputHttpRequestBuilder(ChannelProvider channelProvider,
			HttpRequest request, Map<String,Object> params, HttpHeaders headers, boolean checkStatus, EventExecutor executor) {
		this.request=request;
		this.params=params;
		this.channelProvider=channelProvider;
		this.executor=executor;
		this.checkStatus=checkStatus;
		this.headers=headers;
	}

	@Override
	public Future<Void> send(HttpContent data) {
		return streamOutput.send(data);
	}

	@Override
	public Future<FullHttpResponse> fullResponse() {
		this.futureChannel = channelProvider.channel().thenCombine(ch->writeRequest(ch,request(ch)));
		this.streamOutput = new StreamedOutput(this.futureChannel, executor);
		return futureChannel.thenCombine(this::fullResponseHandler);
	}
	
	private HttpRequest request(ClientChannel ch) {
		if(headers!=null) {
			request.headers().set(headers);
		}
		if(!request.headers().contains(HttpHeaderNames.HOST)) {
			request.headers().set(HttpHeaderNames.HOST,ch.address().host());
		}
		return request.setUri(withParams(request.uri(),params));
	}
	
	private static String withParams(String uri, Map<String, Object> params) {
		if(params == null) {
			return uri;
		}
		
		QueryStringEncoder encoder = new QueryStringEncoder(uri);
		params.forEach((name,value)->encoder.addParam(name, value!=null?value.toString():null));
		return encoder.toString();
	}
	
	private io.netty.util.concurrent.Future<FullHttpResponse> fullResponseHandler(ClientChannel channel){
		Promise<FullHttpResponse> promise = channel.eventLoop().newPromise();
		channel.pipeline().addLast(HANDLER,new FullHttpResponseHandler(HANDLER,channel,checkStatus,promise));
		return promise;
	}
	
	public static io.netty.util.concurrent.Future<ClientChannel> writeRequest(ClientChannel ch, HttpRequest request) {
		ChannelFuture future = ch.writeAndFlush(request);
		Promise<ClientChannel> promise = ch.eventLoop().newPromise();
		Promises.ifSuccessMap(future, promise, v->ch);
		return promise;
	}

	@Override
	public <T> Future<T> fullResponse(Function<FullHttpResponse, T> object) {
		return null;
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
	public Future<ClientChannel> send() {
		this.futureChannel = channelProvider.channel().thenCombine(ch->writeRequest(ch,request(ch)));
		this.streamOutput = new StreamedOutput(this.futureChannel, executor);
		return futureChannel;
	}

	@Override
	public StreamedInputHttpRequestBuilder withCheckStatusCode() {
		return this;
	}
	
	@Override
	public StreamedInputHttpRequestBuilder withIgnoreStatusCode() {
		return this;
	}

}
