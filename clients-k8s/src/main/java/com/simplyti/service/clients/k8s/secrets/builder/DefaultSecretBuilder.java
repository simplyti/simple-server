package com.simplyti.service.clients.k8s.secrets.builder;


import java.util.HashMap;
import java.util.Map;

import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.common.builder.AbstractK8sResourceBuilder;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;
import com.simplyti.service.clients.k8s.secrets.domain.SecretData;

public class DefaultSecretBuilder extends AbstractK8sResourceBuilder<SecretBuilder,Secret> implements SecretBuilder {

	public static final String KIND = "Secret";
	
	private Map<String,SecretData> data;
	private String type;

	public DefaultSecretBuilder(HttpClient client, K8sAPI api,String namespace, String resource) {
		super(client,api,namespace,resource,Secret.class);
	}


	@Override
	protected Secret resource(K8sAPI api, Metadata metadata) {
		return new Secret(KIND, api.version(), metadata, data, type);
	}


	@Override
	public SecretBuilder withData(String key, String str) {
		if(data==null) {
			data = new HashMap<>();
		}
		data.put(key, SecretData.of(str));
		return this;
	}
	
	public SecretBuilder withType(String type) {
		this.type=type;
		return this;
	}

}
