package com.simplyti.service.clients.k8s.common.impl;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;

public class DefaultK8sApi<T extends K8sResource> implements K8sApi<T> {
	
	private final EventLoopGroup eventLoopGroup;
	private final HttpClient http;
	private final K8sAPI api;
	private final String resource;
	
	private final TypeLiteral<KubeList<T>> listType;
	private final TypeLiteral<Event<T>> eventType;

	public DefaultK8sApi(EventLoopGroup eventLoopGroup, HttpClient http, K8sAPI api,String resource,TypeLiteral<KubeList<T>> listType,
			TypeLiteral<Event<T>> eventType) {
		this.http=http;
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
		Observable<T> observable = new DefaultObservable<>(eventLoopGroup.next(),resourceVersion);
		watch(observable);
		return observable;
	}
	
	private void watch(Observable<T> observable) {
		if(observable.isClosed()) {
			return;
		}
		Future<Void> future = http.request()
				.withReadTimeout(30000)
				.get(String.format("%s/%s", api.path(),resource))
				.param("watch")
				.param("resourceVersion",observable.index())
				.stream(EventStreamHandler.NAME,new EventStreamHandler<>(observable,eventType));
		future.addListener(f->watch(observable));
	}

	protected <O> O response(ByteBuf content, TypeLiteral<O> type) {
		byte[] data = new byte[content.readableBytes()];
		content.readBytes(data);
		return JsonIterator.deserialize(data,type);
	}
	
	protected <O> O response(ByteBuf content, Class<O> type) {
		byte[] data = new byte[content.readableBytes()];
		content.readBytes(data);
		return JsonIterator.deserialize(data,type);
	}
	
	protected HttpClient http() {
		return http;
	}
	
	protected K8sAPI api() {
		return api;
	}
	
	protected EventLoopGroup eventLoopGroup() {
		return eventLoopGroup;
	}

}
