package com.simplyti.service.clients.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.simplyti.service.clients.ClientConfig;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.HttpResponseHandler;
import com.simplyti.service.clients.http.handler.ServerEventResponseHandler;
import com.simplyti.service.clients.http.handler.StreamResponseHandler;
import com.simplyti.service.clients.http.sse.ServerEvent;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.concurrent.Future;

public abstract class AbstractFinishableHttpRequest implements FinishableHttpRequest {
	
	protected final InternalClient client;
	protected final Endpoint endpoint;
	private final boolean checkStatusCode;
	private final ClientConfig config;
	
	private final Map<String,String> params;
	
	public AbstractFinishableHttpRequest(InternalClient client, Endpoint endpoint, boolean checkStatusCode, ClientConfig config) {
		this.client = client;
		this.config=config;
		this.checkStatusCode=checkStatusCode;
		this.endpoint = endpoint;
		this.params=new HashMap<>();
	}
	
	@Override
	public FinishableHttpRequest params(Map<String, String> params) {
		this.params.putAll(params);
		return this;
	}
	
	@Override
	public Future<FullHttpResponse> fullResponse() {
		return client.channel(endpoint,channel->{
			channel.pipeline().addLast(new FullHttpResponseHandler(channel,checkStatusCode));
		},request(),config);
	}

	@Override
	public Future<Void> forEach(Consumer<HttpObject> consumer) {
		return client.channel(endpoint,channel->{
			channel.pipeline().addLast(new HttpResponseHandler(channel,consumer));
		},request(),config);
	}

	@Override
	public Future<Void> stream(Consumer<ByteBuf> consumer) {
		return client.channel(endpoint,channel->{
			channel.pipeline().addLast(new StreamResponseHandler(channel,consumer));
		},request(),config);
	}
	
	@Override
	public Future<Void> sse(Consumer<ServerEvent> consumer) {
		return client.channel(endpoint,channel->{
			channel.pipeline().addLast(new ServerEventResponseHandler(channel,consumer));
		},request(),config);
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
