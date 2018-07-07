package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import com.simplyti.service.clients.ClientRequestChannel;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.handler.HttpResponseHandler;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

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
	public StreamedHttpRequest forEach(Consumer<HttpObject> consumer) {
		EventLoop executor = client.eventLoopGroup().next();
		Promise<Void> promise = executor.newPromise();
		Future<ClientRequestChannel<Void>> futureClient = client.<Void>channel(endpoint,channel->{
			channel.pipeline().addLast(new HttpResponseHandler(channel,consumer));
			channel.writeAndFlush(request).addListener(f->client.handleWriteFuture(channel, f, timeoutMillis));
		},promise);
		return new DefaultStreamedHttpRequest(futureClient,executor);
	}

}
