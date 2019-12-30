package com.simplyti.service.clients.http.stream.request;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.http.sse.DefaultServerSentEvents;
import com.simplyti.service.clients.http.sse.ServerSentEvents;
import com.simplyti.service.clients.http.stream.HttpDataStream;
import com.simplyti.service.clients.http.stream.HttpInputStream;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.clients.stream.PendingRequest;
import com.simplyti.service.clients.stream.StreamedOutput;
import com.simplyti.util.concurrent.Future;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

public class DefaultStreamedOutputHttpRequestBuilder implements StreamedInputHttpRequestBuilder, PendingRequest {

	private static final String HANDLER = "handler";
	
	private final HttpRequest request;
	private final Map<String, Object> params;
	private final ChannelProvider channelProvider;
	private final EventExecutor executor;
	private final boolean checkStatus;
	private final HttpHeaders headers;
	
	private Future<ClientChannel> futureChannel;
	private StreamedOutput streamOutput;

	public DefaultStreamedOutputHttpRequestBuilder(ChannelProvider channelProvider,
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
		this.futureChannel = channelProvider.channel();
		this.streamOutput = new StreamedOutput(futureChannel, executor);
		this.streamOutput.send(request());
		return futureChannel.thenCombine(ch->this.fullResponseHandler(ch));
	}
	
	private HttpRequest request() {
		if(headers!=null) {
			request.headers().set(headers);
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
	
	private Promise<FullHttpResponse> fullResponseHandler(ClientChannel channel){
		Promise<FullHttpResponse> promise = channel.eventLoop().newPromise();
		channel.pipeline().addLast(HANDLER,new FullHttpResponseHandler(HANDLER,channel,checkStatus,promise));
		return promise;
	}
	
	private <U> io.netty.util.concurrent.Future<U> addHandlerAndSend(ClientChannel ch, HttpRequest request, Function<ClientChannel,io.netty.util.concurrent.Future<U>> requestHandlerInit) {
		io.netty.util.concurrent.Future<U> future = requestHandlerInit.apply(ch);
		this.streamOutput.send(request).addListener(f->{
			if(!f.isSuccess()) {
				ch.pipeline().fireExceptionCaught(future.cause());
			}
		});
		return future;
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
	public Future<ClientChannel> channel() {
		return channelProvider.channel();
	}
	
	@Override
	public <U> Future<U> addHandlerAndSend(Future<ClientChannel> futureChannel, Supplier<io.netty.util.concurrent.Future<U>> requestHandlerInit){
		return futureChannel.thenCombine(ch->addHandlerAndSend(ch,request(),__->requestHandlerInit.get()));
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
