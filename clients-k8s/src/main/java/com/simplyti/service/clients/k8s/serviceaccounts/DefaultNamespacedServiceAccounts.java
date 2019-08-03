package com.simplyti.service.clients.k8s.serviceaccounts;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultNamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.serviceaccounts.builder.DefaultServiceAccountBuilder;
import com.simplyti.service.clients.k8s.serviceaccounts.builder.ServiceAccountBuilder;
import com.simplyti.service.clients.k8s.serviceaccounts.domain.ServiceAccount;

import io.netty.channel.EventLoopGroup;

public class DefaultNamespacedServiceAccounts extends DefaultNamespacedK8sApi<ServiceAccount> implements NamespacedServiceAccounts {

	public DefaultNamespacedServiceAccounts(EventLoopGroup eventLoopGroup,HttpClient http, Json json, K8sAPI api, String resource, TypeLiteral<KubeList<ServiceAccount>> listType, 
			TypeLiteral<Event<ServiceAccount>> eventType, String namespace) {
		super(eventLoopGroup,http,json,api,namespace,resource,ServiceAccount.class,listType,eventType);
	}

	@Override
	public ServiceAccountBuilder builder() {
		return new DefaultServiceAccountBuilder(http(),json(), api(), namespace(), resource());
	}

}
