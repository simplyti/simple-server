package com.simplyti.service.clients.k8s.common.builder;

import java.util.HashMap;
import java.util.Map;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class AbstractK8sResourceBuilder<B extends K8sResourceBuilder<B,T>, T extends K8sResource> implements K8sResourceBuilder<B,T> {
	
	private final HttpClient client;
	private final Json json;
	private final K8sAPI api;
	private final String namespace;
	private final String resource;
	private final Class<T> type;
	
	private String name;
	private Map<String,String> annotations;
	private Map<String,String> labels;

	public AbstractK8sResourceBuilder(HttpClient client,Json json, K8sAPI api,String namespace, String resource,
			Class<T> type) {
		this.client=client;
		this.json=json;
		this.api=api;
		this.namespace=namespace;
		this.resource=resource;
		this.type=type;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withName(String name) {
		this.name=name;
		return (B) this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public B withAnnotation(String ann, String value) {
		if(this.annotations==null) {
			this.annotations=new HashMap<>();
		}
		this.annotations.put(ann,value);
		return (B) this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public B withLabel(String name, String value) {
		if(this.labels==null) {
			this.labels=new HashMap<>();
		}
		this.labels.put(name,value);
		return (B) this;
	}
	
	@Override
	public Future<T> build() {
		return client.request()
			.post(String.format("%s/namespaces/%s/%s",api.path(),namespace,resource))
			.withBody(this::body)
			.fullResponse(f->response(f, type));
	}
	
	private ByteBuf body(ByteBuf buffer) {
		json.serialize(resource(api,Metadata.builder()
				.annotations(annotations)
				.namespace(namespace)
				.labels(labels)
				.name(name)
				.build()),buffer);
		return buffer;
	}

	protected abstract T resource(K8sAPI api, Metadata metadata);

	private <O> O response(FullHttpResponse response, Class<O> type) {
		return json.deserialize(response.content(), type);
	}

}
