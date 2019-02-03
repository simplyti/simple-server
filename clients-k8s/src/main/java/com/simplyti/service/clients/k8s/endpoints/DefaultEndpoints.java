package com.simplyti.service.clients.k8s.endpoints;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.endpoints.builder.DefaultEndpointBuilder;
import com.simplyti.service.clients.k8s.endpoints.builder.EndpointBuilder;
import com.simplyti.service.clients.k8s.endpoints.domain.Endpoint;

import io.netty.channel.EventLoopGroup;

public class DefaultEndpoints extends DefaultK8sApi<Endpoint> implements Endpoints {
	
	private static final String RESOURCE = "endpoints";
	private static final TypeLiteral<KubeList<Endpoint>> LIST_TYPE = new TypeLiteral<KubeList<Endpoint>>() {};
	private static final TypeLiteral<Event<Endpoint>> EVENT_TYPE = new TypeLiteral<Event<Endpoint>>() {};

	public DefaultEndpoints(EventLoopGroup eventLoopGroup,HttpClient http) {
		super(eventLoopGroup,http,K8sAPI.V1, RESOURCE,LIST_TYPE,EVENT_TYPE);
	}

	@Override
	public NamespacedEndpoints namespace(String namespace) {
		return new DefaultNamespacedEndpoints(eventLoopGroup(),http(),K8sAPI.V1,RESOURCE,LIST_TYPE,EVENT_TYPE,namespace);
	}

	@Override
	public EndpointBuilder builder(String namespace) {
		return new DefaultEndpointBuilder(http(),K8sAPI.V1,namespace,RESOURCE);
	}


}
