package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import com.simplyti.service.clients.ClientResponseFuture;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.handler.HttpResponseHandler;

import io.netty.handler.codec.http.HttpRequest;

public class DefaultFinishableStreamedHttpRequest implements FinishableStreamedHttpRequest {

	private final InternalClient client;
	private final Endpoint endpoint;
	private final long timeoutMillis;
	private final HttpRequest request;

	public DefaultFinishableStreamedHttpRequest(InternalClient client, Endpoint endpoint, HttpRequest request, long timeoutMillis) {
		this.client = client;
		this.timeoutMillis=timeoutMillis;
		this.endpoint = endpoint;
		this.request = request;
	}

	@Override
	public StreamedHttpRequest forEach(Consumer<Object> consumer) {
		ClientResponseFuture<Void> future = client.channel(endpoint,request,clientChannel->
			clientChannel.pipeline().addLast(new HttpResponseHandler(clientChannel,consumer)),timeoutMillis);
		return new DefaultStreamedHttpRequest(future);
	}

}
