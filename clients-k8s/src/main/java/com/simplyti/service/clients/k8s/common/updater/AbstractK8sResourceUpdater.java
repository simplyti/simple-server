package com.simplyti.service.clients.k8s.common.updater;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

public class AbstractK8sResourceUpdater<T extends K8sResource> {
	
	private final List<JsonPatch> patches = new ArrayList<>();
	
	private final HttpClient client;
	private final Json json;
	private final K8sAPI api;
	private final String namespace;
	private final String resource;
	private final Class<T> type;
	
	private final String name;
	
	public AbstractK8sResourceUpdater(HttpClient client, Json json, K8sAPI api,String namespace, String resource,
			String name, Class<T> type) {
		this.client=client;
		this.json=json;
		this.api=api;
		this.namespace=namespace;
		this.name=name;
		this.resource=resource;
		this.type=type;
	}
	
	protected void addPatch(JsonPatch patch) {
		this.patches.add(patch);
	}
	
	protected void setPatch(JsonPatch patch) {
		this.patches.clear();
		this.patches.add(patch);
	}
	
	public Future<T> update() {
		return client.request()
				.withHeader(HttpHeaderNames.CONTENT_TYPE.toString(),"application/json-patch+json")
				.patch(String.format("%s/namespaces/%s/%s/%s",api.path(),namespace,resource,name))
				.withBodyWriter(this::body)
				.fullResponse(f->response(f, type));
	}
	
	private ByteBuf body(ByteBuf buffer) {
		json.serialize(this.patches,buffer);
		return buffer;
	}
	
	private <O> O response(FullHttpResponse response, Class<O> type) {
		return json.deserialize(response.content(),type);
	}

}
