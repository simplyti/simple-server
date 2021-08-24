package com.simplyti.service.clients.k8s.namespaces.builder;

import java.util.HashMap;
import java.util.Map;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.namespaces.domain.Namespace;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultNamespacesBuilder implements NamespacesBuilder {
	
	private static final String KIND = "Namespace";
	
	private final HttpClient client;
	private final Json json;
	private final K8sAPI api;
	
	private String name;
	private Map<String,String> annotations;
	private Map<String,String> labels;


	public DefaultNamespacesBuilder(HttpClient client, Json json, K8sAPI api) {
		this.client=client;
		this.json=json;
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
	public NamespacesBuilder withLabel(String name, String value) {
		if(this.labels==null) {
			this.labels=new HashMap<>();
		}
		this.labels.put(name,value);
		return this;
	}


	@Override
	public Future<Namespace> build() {
		return client.request()
				.post(String.format("%s/namespaces",api.path()))
				.withBodyWriter(this::body)
				.fullResponse(this::response);
	}
	
	private ByteBuf body(ByteBuf buffer) {
		json.serialize(new Namespace(KIND,api.version(),Metadata.builder()
				.labels(labels)
				.name(name)
				.build()),buffer);
		return buffer;
	}
	
	private Namespace response(FullHttpResponse response) {
		return json.deserialize(response.content(),Namespace.class);
	}

}
