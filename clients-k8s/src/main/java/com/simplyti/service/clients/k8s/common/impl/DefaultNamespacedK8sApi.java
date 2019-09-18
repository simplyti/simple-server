package com.simplyti.service.clients.k8s.common.impl;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.NamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.domain.KubeClientException;
import com.simplyti.service.clients.k8s.common.domain.Status;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;

public class DefaultNamespacedK8sApi<T extends K8sResource> extends DefaultK8sApi<T> implements NamespacedK8sApi<T> {
	
	private static final TypeLiteral<Status> STATUS_TYPE = new TypeLiteral<Status>() {};
	
	private final EventLoopGroup eventLoopGroup;
	private final HttpClient http;
	private final Json json;
	private final K8sAPI api;
	private final String namespace;
	private final String resource;
	
	private final TypeLiteral<KubeList<T>> listType;
	private final TypeLiteral<Event<T>> eventType;
	private final Class<T> type;

	public DefaultNamespacedK8sApi(EventLoopGroup eventLoopGroup, HttpClient http, Json json, K8sAPI api,String namespace, String resource, 
			Class<T> type, TypeLiteral<KubeList<T>> listType, TypeLiteral<Event<T>> eventType) {
		super(eventLoopGroup, http, json,api,resource,listType,eventType);
		this.eventLoopGroup=eventLoopGroup;
		this.http=http;
		this.json=json;
		this.api=api;
		this.namespace=namespace;
		this.resource=resource;
		this.listType=listType;
		this.eventType=eventType;
		this.type=type;
	}

	@Override
	public Future<KubeList<T>> list() {
		return http.request()
				.get(String.format("%s/namespaces/%s/%s",api.path(),namespace,resource))
				.fullResponse(f->response(f.content(), listType));
	}

	@Override
	public Future<T> get(String name) {
		return http.request()
				.get(String.format("%s/namespaces/%s/%s/%s",api.path(),namespace,resource,name))
				.fullResponse(f->response(f.content(), type));
	}
	
	private void watch(String name, InternalObservable<T> observable) {
		if(observable.isClosed()) {
			return;
		}
		Future<Void> future = http.request()
				.withReadTimeout(30000)
				.get(String.format("%s/watch/namespaces/%s/%s/%s",api.path(),namespace,resource,name))
				.param("watch")
				.param("resourceVersion",observable.index())
				.stream(EventStreamHandler.NAME,client->new EventStreamHandler<>(client,json,observable,eventType));
		future.addListener(f->{
			if(f.isSuccess()) {
				watch(name,observable);
			}else {
				if(f.cause() instanceof KubeClientException) {
					observable.error(f.cause());
				}else {
					observable.executor().schedule(()->watch(name,observable),1,TimeUnit.SECONDS);
				}
			}
		});
	}

	@Override
	public Observable<T> watch(String name, String resourceVersion) {
		InternalObservable<T> observable = new DefaultObservable<>(eventLoopGroup.next(),resourceVersion);
		watch(name,observable);
		return observable;
	}
	
	@Override
	public Future<Status> delete(String name) {
		return http.request()
				.delete(String.format("%s/namespaces/%s/%s/%s",api.path(),namespace,resource,name))
				.fullResponse(f->response(f.content(), STATUS_TYPE));
	}
	
	
	protected ByteBuf body(ByteBufAllocator ctx, T resource) {
		ByteBuf buffer = ctx.buffer();
		json.serialize(resource,buffer);
		return buffer;
	}
	
	protected <O> O response(FullHttpResponse response, Class<O> type) {
		return json.deserialize(response.content(),type);
	}
	
	protected String namespace() {
		return namespace;
	}
	
	protected String resource() {
		return resource;
	}

}
