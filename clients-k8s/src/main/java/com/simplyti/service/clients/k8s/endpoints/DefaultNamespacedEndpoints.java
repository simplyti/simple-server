package com.simplyti.service.clients.k8s.endpoints;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultNamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.endpoints.builder.DefaultEndpointBuilder;
import com.simplyti.service.clients.k8s.endpoints.builder.EndpointBuilder;
import com.simplyti.service.clients.k8s.endpoints.domain.Endpoint;
import com.simplyti.service.clients.k8s.endpoints.updater.DefaultEndpointUpdater;
import com.simplyti.service.clients.k8s.endpoints.updater.EndpointUpdater;

import io.netty.channel.EventLoopGroup;

public class DefaultNamespacedEndpoints extends DefaultNamespacedK8sApi<Endpoint> implements NamespacedEndpoints {

	public DefaultNamespacedEndpoints(EventLoopGroup eventLoopGroup,HttpClient http, Json json, K8sAPI api, String resource, TypeLiteral<KubeList<Endpoint>> listType, 
			TypeLiteral<Event<Endpoint>> eventType, String namespace) {
		super(eventLoopGroup, http,json,api,namespace,resource,Endpoint.class,listType,eventType);
	}

	@Override
	public EndpointBuilder builder() {
		return new DefaultEndpointBuilder(http(),json(), api(), namespace(), resource());
	}

	@Override
	public EndpointUpdater update(String name) {
		return new DefaultEndpointUpdater(http(),json(), api(), namespace(), resource(),name);
	}

}
