package com.simplyti.service.clients.k8s.namespaces.builder;

import java.util.HashMap;
import java.util.Map;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.namespaces.domain.Namespace;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;

public class DefaultNamespacesBuilder implements NamespacesBuilder {
	
	private static final String KIND = "Namespace";
	
	private final HttpClient client;
	private final K8sAPI api;
	
	private String name;
	private Map<String,String> annotations;


	public DefaultNamespacesBuilder(HttpClient client, K8sAPI api) {
		this.client=client;
		this.api=api;
	}

	@Override
	public NamespacesBuilder withName(String name) {
		this.name=name;
		return this;
	}
	
	@Override
	public NamespacesBuilder withAnnotation(String ann, String value) {
		if(this.annotations==null) {
			this.annotations=new HashMap<>();
		}
		this.annotations.put(ann,value);
		return this;
	}

	@Override
	public Future<Namespace> build() {
		return client.request()
				.post(String.format("%s/namespaces",api.path()))
				.body(this::body)
				.fullResponse(this::response);
	}
	
	private ByteBuf body(ByteBufAllocator ctx) {
		ByteBuf buffer = ctx.buffer();
		JsonStream.serialize(new Namespace(KIND,api.version(),Metadata.builder()
				.name(name)
				.build()),new ByteBufOutputStream(buffer));
		return buffer;
	}
	
	private Namespace response(FullHttpResponse response) {
		byte[] data = new byte[response.content().readableBytes()];
		response.content().readBytes(data);
		return JsonIterator.deserialize(data,Namespace.class);
	}

}
