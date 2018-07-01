package com.simplyti.service.clients.http.request;

import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.Maps;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.HttpResponseHandler;
import com.simplyti.service.clients.http.handler.StreamResponseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.concurrent.Future;

public abstract class AbstractFinishableHttpRequest implements FinishableHttpRequest {
	
	protected final InternalClient client;
	protected final Endpoint endpoint;
	private final boolean checkStatusCode;
	private final long timeoutMillis;
	
	private final Map<String,String> params;
	
	public AbstractFinishableHttpRequest(InternalClient client, Endpoint endpoint, boolean checkStatusCode,
			long timeoutMillis) {
		this.client = client;
		this.timeoutMillis=timeoutMillis;
		this.checkStatusCode=checkStatusCode;
		this.endpoint = endpoint;
		this.params=Maps.newHashMap();
	}
	
	@Override
	public FinishableHttpRequest params(Map<String, String> params) {
		this.params.putAll(params);
		return this;
	}
	
	@Override
	public Future<FullHttpResponse> fullResponse() {
		return client.<FullHttpResponse>channel(endpoint,request(),clientChannel->
			clientChannel.pipeline().addLast(new FullHttpResponseHandler(clientChannel,checkStatusCode)),
			timeoutMillis).future();
	}

	@Override
	public Future<Void> forEach(Consumer<Object> consumer) {
		return client.<Void>channel(endpoint,request(),clientChannel->
			clientChannel.pipeline().addLast(new HttpResponseHandler(clientChannel,consumer)),
			timeoutMillis).future();
	}

	@Override
	public Future<Void> stream(Consumer<ByteBuf> consumer) {
		return client.<Void>channel(endpoint,request(),clientChannel->
			clientChannel.pipeline().addLast(new StreamResponseHandler(clientChannel,consumer)),
			timeoutMillis).future();
	}
	
	private FullHttpRequest request() {
		if(params.isEmpty()) {
			return request0();
		}
		
		FullHttpRequest request = request0();
		QueryStringEncoder encoder = new QueryStringEncoder(request.uri());
		params.forEach(encoder::addParam);
		return request.setUri(encoder.toString());
	}
	
	protected abstract FullHttpRequest request0();

}
