package com.simplyti.service.clients.k8s.serviceaccounts.builder;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.common.builder.AbstractK8sResourceBuilder;
import com.simplyti.service.clients.k8s.serviceaccounts.domain.ServiceAccount;

public class DefaultServiceAccountBuilder extends AbstractK8sResourceBuilder<ServiceAccountBuilder,ServiceAccount> implements ServiceAccountBuilder {

	public static final String KIND = "ServiceAccount";
	
	public DefaultServiceAccountBuilder(HttpClient client,Json json, K8sAPI api,String namespace, String resource) {
		super(client,json,api,namespace,resource,ServiceAccount.class);
	}

	@Override
	protected ServiceAccount resource(K8sAPI api, Metadata metadata) {
		return new ServiceAccount(KIND, api.version(), metadata);
	}
	
}
