package com.simplyti.service.clients.k8s.common.updater;

import java.util.ArrayList;
import java.util.List;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.K8sResource;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.concurrent.Future;

public class AbstractK8sResourceUpdater<T extends K8sResource> {
	
	private final List<JsonPatch> patches = new ArrayList<>();
	
	private final HttpClient client;
	private final K8sAPI api;
	private final String namespace;
	private final String resource;
	private final Class<T> type;
	
	private final String name;

	
	public AbstractK8sResourceUpdater(HttpClient client, K8sAPI api,String namespace, String resource,
			String name, Class<T> type) {
		this.client=client;
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
				.body(this::body)
				.fullResponse(f->response(f, type));
	}
	
	private ByteBuf body(ByteBufAllocator ctx) {
		ByteBuf buffer = ctx.buffer();
		JsonStream.serialize(this.patches,new ByteBufOutputStream(buffer));
		return buffer;
	}
	
	private <O> O response(FullHttpResponse response, Class<O> type) {
		byte[] data = new byte[response.content().readableBytes()];
		response.content().readBytes(data);
		return JsonIterator.deserialize(data,type);
	}

}
