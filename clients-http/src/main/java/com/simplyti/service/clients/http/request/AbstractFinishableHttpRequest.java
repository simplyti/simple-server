package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.HttpResponseHandler;
import com.simplyti.service.clients.http.handler.StreamResponseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Future;

public abstract class AbstractFinishableHttpRequest implements FinishableHttpRequest {
	
	protected final InternalClient client;
	protected final Endpoint endpoint;
	private final boolean checkStatusCode;
	private final long timeoutMillis;
	
	public AbstractFinishableHttpRequest(InternalClient client, Endpoint endpoint, boolean checkStatusCode,
			long timeoutMillis) {
		this.client = client;
		this.timeoutMillis=timeoutMillis;
		this.checkStatusCode=checkStatusCode;
		this.endpoint = endpoint;
	}
	
	@Override
	public Future<FullHttpResponse> fullResponse() {
		return client.channel(endpoint,request(),clientChannel->
			clientChannel.pipeline().addLast(new FullHttpResponseHandler(clientChannel,checkStatusCode)),
			timeoutMillis);
	}

	@Override
	public Future<Void> forEach(Consumer<HttpObject> consumer) {
		return client.channel(endpoint,request(),clientChannel->
			clientChannel.pipeline().addLast(new HttpResponseHandler(clientChannel,consumer)),
			timeoutMillis);
	}

	@Override
	public Future<Void> stream(Consumer<ByteBuf> consumer) {
		return client.channel(endpoint,request(),clientChannel->
			clientChannel.pipeline().addLast(new StreamResponseHandler(clientChannel,consumer)),
			timeoutMillis);
	}
	
	protected abstract FullHttpRequest request();

}
