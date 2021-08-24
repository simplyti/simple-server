package com.simplyti.service.clients.k8s.serviceaccounts;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.serviceaccounts.builder.DefaultServiceAccountBuilder;
import com.simplyti.service.clients.k8s.serviceaccounts.builder.ServiceAccountBuilder;
import com.simplyti.service.clients.k8s.serviceaccounts.domain.ServiceAccount;

import io.netty.channel.EventLoopGroup;

public class DefaultServiceAccounts extends DefaultK8sApi<ServiceAccount> implements ServiceAccounts {
	
	private static final String RESOURCE = "serviceaccounts";
	private static final TypeLiteral<KubeList<ServiceAccount>> LIST_TYPE = new TypeLiteral<KubeList<ServiceAccount>>() {};
	private static final TypeLiteral<Event<ServiceAccount>> EVENT_TYPE = new TypeLiteral<Event<ServiceAccount>>() {};

	public DefaultServiceAccounts(EventLoopGroup eventLoopGroup, HttpClient http, long timeoutMillis, Json json) {
		super(eventLoopGroup,http,timeoutMillis,json,K8sAPI.V1, RESOURCE,LIST_TYPE,EVENT_TYPE);
	}

	@Override
	public NamespacedServiceAccounts namespace(String namespace) {
		return new DefaultNamespacedServiceAccounts(eventLoopGroup(),http(),timeoutMillis(), json(),K8sAPI.V1,RESOURCE,LIST_TYPE,EVENT_TYPE,namespace);
	}

	@Override
	public ServiceAccountBuilder builder(String namespace) {
		return new DefaultServiceAccountBuilder(http(),json(), K8sAPI.V1, namespace, RESOURCE);
	}

}