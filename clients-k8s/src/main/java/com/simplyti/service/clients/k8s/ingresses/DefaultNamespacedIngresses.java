package com.simplyti.service.clients.k8s.ingresses;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultNamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.ingresses.builder.DefaultIngressBuilder;
import com.simplyti.service.clients.k8s.ingresses.builder.IngressBuilder;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;
import com.simplyti.service.clients.k8s.ingresses.updater.DefaultIngressesUpdater;
import com.simplyti.service.clients.k8s.ingresses.updater.IngressUpdater;

import io.netty.channel.EventLoopGroup;

public class DefaultNamespacedIngresses extends DefaultNamespacedK8sApi<Ingress> implements NamespacedIngresses {

	public DefaultNamespacedIngresses(EventLoopGroup eventLoopGroup,HttpClient http, K8sAPI api, String resource, TypeLiteral<KubeList<Ingress>> listType, 
			TypeLiteral<Event<Ingress>> eventType, String namespace) {
		super(eventLoopGroup,http,api,namespace,resource,Ingress.class,listType,eventType);
	}

	@Override
	public IngressBuilder builder() {
		return new DefaultIngressBuilder(http(),K8sAPI.BETA1,namespace(),resource());
	}

	@Override
	public IngressUpdater update(String name) {
		return new DefaultIngressesUpdater(http(), api(), namespace(), resource(),name);
	}

}
