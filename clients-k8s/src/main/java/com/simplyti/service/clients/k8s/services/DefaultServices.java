package com.simplyti.service.clients.k8s.services;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.services.builder.DefaultServiceBuilder;
import com.simplyti.service.clients.k8s.services.builder.ServiceBuilder;
import com.simplyti.service.clients.k8s.services.domain.Service;

import io.netty.channel.EventLoopGroup;

public class DefaultServices extends DefaultK8sApi<Service> implements Services {
	
	private static final String RESOURCE = "services";
	private static final TypeLiteral<KubeList<Service>> LIST_TYPE = new TypeLiteral<KubeList<Service>>() {};
	private static final TypeLiteral<Event<Service>> EVENT_TYPE = new TypeLiteral<Event<Service>>() {};

	public DefaultServices(EventLoopGroup eventLoopGroup, HttpClient http, long timeoutMillis, Json json) {
		super(eventLoopGroup,http,timeoutMillis,json,K8sAPI.V1,RESOURCE,LIST_TYPE,EVENT_TYPE);
	}

	@Override
	public NamespacedServices namespace(String namespace) {
		return new DefaultNamespacedServices(eventLoopGroup(),http(),timeoutMillis(),json(),K8sAPI.V1,RESOURCE,LIST_TYPE,EVENT_TYPE,namespace);
	}

	@Override
	public ServiceBuilder builder(String namespace) {
		return new DefaultServiceBuilder(http(),json(),K8sAPI.V1,namespace,RESOURCE);
	}


}
