package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import com.simplyti.service.clients.ClientFuture;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.handler.HttpResponseHandler;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public class DefaultStreamedHttpRequest implements StreamedHttpRequest {

	private final InternalClient client;
	private final Endpoint endpoint;
	private final long timeoutMillis;
	private final HttpRequest request;

	public DefaultStreamedHttpRequest(InternalClient client, Endpoint endpoint, HttpRequest request, long timeoutMillis) {
		this.client = client;
		this.timeoutMillis=timeoutMillis;
		this.endpoint = endpoint;
		this.request = request;
	}

	@Override
	public FinishableStreamedHttpRequest forEach(Consumer<HttpObject> consumer) {
		ClientFuture<Void> future = client.channel(endpoint,request,clientChannel->
			clientChannel.pipeline().addLast(new HttpResponseHandler(clientChannel,consumer)),timeoutMillis);
		return new DefaultFinishableStreamedHttpRequest(future);
	}

}
