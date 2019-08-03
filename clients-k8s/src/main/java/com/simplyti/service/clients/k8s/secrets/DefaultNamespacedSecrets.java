package com.simplyti.service.clients.k8s.secrets;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultNamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.secrets.builder.DefaultSecretBuilder;
import com.simplyti.service.clients.k8s.secrets.builder.SecretBuilder;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;

import io.netty.channel.EventLoopGroup;

public class DefaultNamespacedSecrets extends DefaultNamespacedK8sApi<Secret> implements NamespacedSecrets {

	public DefaultNamespacedSecrets(EventLoopGroup eventLoopGroup,HttpClient http,Json json, K8sAPI api, String resource, TypeLiteral<KubeList<Secret>> listType, 
			TypeLiteral<Event<Secret>> eventType, String namespace) {
		super(eventLoopGroup,http,json,api,namespace,resource,Secret.class,listType,eventType);
	}

	@Override
	public SecretBuilder builder() {
		return new DefaultSecretBuilder(http(),json(), api(), namespace(), resource());
	}


}
