package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import com.simplyti.service.clients.ClientConfig;
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
	private final ClientConfig config;
	private final HttpRequest request;

	public DefaultFinishableStreamedHttpRequest(InternalClient client, Endpoint endpoint, HttpRequest request, ClientConfig config) {
		this.client = client;
		this.config=config;
		this.request = request;
	}

	@Override
	public StreamedHttpRequest forEach(Consumer<HttpObject> consumer) {
		EventLoop executor = client.eventLoopGroup().next();
		Promise<Void> promise = executor.newPromise();
		Future<ClientRequestChannel<Void>> futureClient = client.<Void>channel(config,channel->{
			channel.pipeline().addLast(new HttpResponseHandler(channel,consumer));
			channel.writeAndFlush(request).addListener(f->client.handleWriteFuture(channel, f, config.timeoutMillis()));
		},promise);
		return new DefaultStreamedHttpRequest(futureClient,executor);
	}

}
