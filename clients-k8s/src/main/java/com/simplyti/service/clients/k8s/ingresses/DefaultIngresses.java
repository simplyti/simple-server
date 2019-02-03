package com.simplyti.service.clients.k8s.ingresses;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.ingresses.builder.DefaultIngressBuilder;
import com.simplyti.service.clients.k8s.ingresses.builder.IngressBuilder;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;

import io.netty.channel.EventLoopGroup;

public class DefaultIngresses extends DefaultK8sApi<Ingress> implements Ingresses {
	
	private static final String RESOURCE = "ingresses";
	private static final TypeLiteral<KubeList<Ingress>> LIST_TYPE = new TypeLiteral<KubeList<Ingress>>() {};
	private static final TypeLiteral<Event<Ingress>> EVENT_TYPE = new TypeLiteral<Event<Ingress>>() {};

	public DefaultIngresses(EventLoopGroup eventLoopGroup, HttpClient http) {
		super(eventLoopGroup,http,K8sAPI.BETA1, RESOURCE,LIST_TYPE,EVENT_TYPE);
	}

	@Override
	public NamespacedIngresses namespace(String namespace) {
		return new DefaultNamespacedIngresses(eventLoopGroup(),http(),K8sAPI.BETA1,RESOURCE,LIST_TYPE,EVENT_TYPE,namespace);
	}

	@Override
	public IngressBuilder builder(String namespace) {
		return new DefaultIngressBuilder(http(),K8sAPI.BETA1,namespace,RESOURCE);
	}

}
