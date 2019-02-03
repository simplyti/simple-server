package com.simplyti.service.clients.k8s.common.impl;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.NamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.domain.Status;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;

public class DefaultNamespacedK8sApi<T extends K8sResource> extends DefaultK8sApi<T> implements NamespacedK8sApi<T> {
	
	private static final TypeLiteral<Status> STATUS_TYPE = new TypeLiteral<Status>() {};
	
	private final HttpClient http;
	private final K8sAPI api;
	private final String namespace;
	private final String resource;
	
	private final TypeLiteral<KubeList<T>> listType;
	private final Class<T> type;

	public DefaultNamespacedK8sApi(EventLoopGroup eventLoopGroup, HttpClient http, K8sAPI api,String namespace, String resource, 
			Class<T> type, TypeLiteral<KubeList<T>> listType, TypeLiteral<Event<T>> eventType) {
		super(eventLoopGroup, http,api,resource,listType,eventType);
		this.http=http;
		this.api=api;
		this.namespace=namespace;
		this.resource=resource;
		this.listType=listType;
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

	@Override
	public Future<Status> delete(String name) {
		return http.request()
				.delete(String.format("%s/namespaces/%s/%s/%s",api.path(),namespace,resource,name))
				.fullResponse(f->response(f.content(), STATUS_TYPE));
	}
	
	protected String namespace() {
		return namespace;
	}
	
	protected String resource() {
		return resource;
	}

}
