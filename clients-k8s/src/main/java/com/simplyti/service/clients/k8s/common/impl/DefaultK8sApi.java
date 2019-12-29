package com.simplyti.service.clients.k8s.common.impl;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.domain.KubeClientException;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;

public class DefaultK8sApi<T extends K8sResource> implements K8sApi<T> {
	
	private final EventLoopGroup eventLoopGroup;
	private final HttpClient http;
	private final Json json;
	private final K8sAPI api;
	private final String resource;
	
	private final TypeLiteral<KubeList<T>> listType;
	private final TypeLiteral<Event<T>> eventType;

	public DefaultK8sApi(EventLoopGroup eventLoopGroup, HttpClient http, Json json, K8sAPI api,String resource,TypeLiteral<KubeList<T>> listType,
			TypeLiteral<Event<T>> eventType) {
		this.http=http;
		this.json=json;
		this.api=api;
		this.resource=resource;
		this.listType=listType;
		this.eventType=eventType;
		this.eventLoopGroup=eventLoopGroup;
	}

	@Override
	public Future<KubeList<T>> list() {
		return http.request()
				.get(String.format("%s/%s", api.path(),resource))
				.fullResponse(f->response(f.content(), listType));
	}
	
	@Override
	public Observable<T> watch(String resourceVersion) {
		InternalObservable<T> observable = new DefaultObservable<>(eventLoopGroup.next(),resourceVersion);
		watch(observable);
		return observable;
	}
	
	private void watch(InternalObservable<T> observable) {
		if(observable.isClosed()) {
			return;
		}
		Future<Void> future = http.request()
				.withReadTimeout(30000)
				.get(String.format("%s/%s", api.path(),resource))
				.param("watch")
				.param("resourceVersion",observable.index())
				.stream().withHandler(client->client.pipeline().addLast(EventStreamHandler.EVENT_HANDLER,
						new EventStreamHandler<>(EventStreamHandler.EVENT_HANDLER,json,observable,eventType)));
		future.addListener(f->{
			if(f.isSuccess()) {
				watch(observable);
			}else {
				if(f.cause() instanceof KubeClientException) {
					observable.error(f.cause());
				}else {
					observable.executor().schedule(()->watch(observable),1,TimeUnit.SECONDS);
				}
			}
		});
	}

	protected <O> O response(ByteBuf content, TypeLiteral<O> type) {
		return json.deserialize(content,type);
	}
	
	protected <O> O response(ByteBuf content, Class<O> type) {
		return json.deserialize(content,type);
	}
	
	protected HttpClient http() {
		return http;
	}
	
	protected Json json() {
		return json;
	}
	
	protected K8sAPI api() {
		return api;
	}
	
	protected EventLoopGroup eventLoopGroup() {
		return eventLoopGroup;
	}

}
