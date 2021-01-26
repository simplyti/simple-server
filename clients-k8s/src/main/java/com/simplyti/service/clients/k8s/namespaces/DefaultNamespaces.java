package com.simplyti.service.clients.k8s.namespaces;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.domain.Status;
import com.simplyti.service.clients.k8s.common.impl.DefaultK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.namespaces.builder.DefaultNamespacesBuilder;
import com.simplyti.service.clients.k8s.namespaces.builder.NamespacesBuilder;
import com.simplyti.service.clients.k8s.namespaces.domain.Namespace;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;

public class DefaultNamespaces extends DefaultK8sApi<Namespace> implements Namespaces {

	private static final String RESOURCE = "namespaces";
	private static final TypeLiteral<KubeList<Namespace>> LIST_TYPE = new TypeLiteral<KubeList<Namespace>>() {};
	private static final TypeLiteral<Event<Namespace>> EVENT_TYPE = new TypeLiteral<Event<Namespace>>() {};

	public DefaultNamespaces(EventLoopGroup eventLoopGroup, HttpClient http,long timeoutMillis, Json json) {
		super(eventLoopGroup,http,timeoutMillis,json,K8sAPI.V1, RESOURCE,LIST_TYPE,EVENT_TYPE);
	}

	@Override
	public Future<Status> delete(String name) {
		return http().request().delete(String.format("%s/namespaces/%s",api().path(),name))
			.fullResponse(this::response);
	}
	
	private Status response(FullHttpResponse response) {
		return json().deserialize(response.content(),Status.class);
	}

	@Override
	public NamespacesBuilder builder() {
		return new DefaultNamespacesBuilder(http(),json(), K8sAPI.V1);
	}

}
