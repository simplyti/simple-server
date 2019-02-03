package com.simplyti.service.clients.k8s.services;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultNamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.services.builder.DefaultServiceBuilder;
import com.simplyti.service.clients.k8s.services.builder.ServiceBuilder;
import com.simplyti.service.clients.k8s.services.domain.Service;
import com.simplyti.service.clients.k8s.services.updater.DefaultServiceUpdater;
import com.simplyti.service.clients.k8s.services.updater.ServicesUpdater;

import io.netty.channel.EventLoopGroup;

public class DefaultNamespacedServices extends DefaultNamespacedK8sApi<Service> implements NamespacedServices {

	public DefaultNamespacedServices(EventLoopGroup eventLoopGroup,HttpClient http, K8sAPI api, String resource,TypeLiteral<KubeList<Service>> listType,
			TypeLiteral<Event<Service>> eventType, String namespace) {
		super(eventLoopGroup,http,api,namespace,resource,Service.class,listType,eventType);
	}

	@Override
	public ServiceBuilder builder() {
		return new DefaultServiceBuilder(http(), api(), namespace(), resource());
	}

	@Override
	public ServicesUpdater update(String name) {
		return new DefaultServiceUpdater(http(), api(), namespace(), resource(),name);
	}

}
