package com.simplyti.service.clients.k8s.pods;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.request.FinishableHttpRequestBuilder;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.concurrent.Promise;

public class DefaultLogStream implements LogStream {

	private final HttpClient http;
	private final String pod;
	private final String container;
	private final K8sAPI api;
	private final String namespace;
	private final String resource;
	private final EventLoop loop;
	private final long readTimeoutSeconds;
	
	private boolean closed;
	private ClientChannel client;

	public DefaultLogStream(EventLoop loop, K8sAPI api, HttpClient http, long readTimeoutMillis, String pod, String container,String namespace, String resource) {
		this.http=http;
		this.pod = pod;
		this.container = container;
		this.api=api;
		this.namespace=namespace;
		this.resource=resource;
		this.loop=loop;
		this.readTimeoutSeconds = TimeUnit.MILLISECONDS.toSeconds(readTimeoutMillis);
	}

	@Override
	public Future<Void> follow(Consumer<ByteBuf> consumer) {
		Promise<Void> promise = loop.newPromise();
		return followLog(promise,false,consumer);
	}


	private Future<Void> followLog(Promise<Void> promise, boolean isContinue, Consumer<ByteBuf> consumer) {
		FinishableHttpRequestBuilder builder = http.request()
			.get(String.format("%s/namespaces/%s/%s/%s/log",api.path(),namespace,resource, pod))
			.param("follow",true);

		if(this.container != null) {
			builder.param("container", this.container);
		}
		
		if(this.container != null) {
			builder.param("container", this.container);
		}
		
		if(isContinue) {
			builder.param("sinceSeconds",readTimeoutSeconds);
		}
		
		builder.stream()
		.onConnect(this::onConnect)
		.forEach(consumer::accept)
		.thenApply(promise::setSuccess)
		.onError(cause->{
			if(closed) {
				promise.setSuccess(null);
			} else if(cause instanceof ReadTimeoutException ) {
				followLog(promise,true,consumer);
			} else {
				promise.setFailure(cause);
			}
		});
		return new DefaultFuture<>(promise, loop);
	}
	
	private void onConnect(ClientChannel ch) {
		this.client=ch;
		if(loop.inEventLoop()) {
			checkClose0();
		} else {
			loop.execute(this::checkClose0);
		}
	}

	private void checkClose0() {
		if(closed) {
			client.close();
		}
	}

	@Override
	public void close() {
		this.closed = true;
		if(loop.inEventLoop()) {
			close0();
		} else {
			loop.execute(this::close0);
		}
	}

	private void close0() {
		if(client!=null) {
			client.close();
		}
	}

}
