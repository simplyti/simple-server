package com.simplyti.service.clients.k8s.secrets;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.secrets.builder.DefaultSecretBuilder;
import com.simplyti.service.clients.k8s.secrets.builder.SecretBuilder;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;

import io.netty.channel.EventLoopGroup;

public class DefaultSecrets extends DefaultK8sApi<Secret> implements Secrets {
	
	private static final String RESOURCE = "secrets";
	private static final TypeLiteral<KubeList<Secret>> LIST_TYPE = new TypeLiteral<KubeList<Secret>>() {};
	private static final TypeLiteral<Event<Secret>> EVENT_TYPE = new TypeLiteral<Event<Secret>>() {};

	public DefaultSecrets(EventLoopGroup eventLoopGroup, HttpClient http) {
		super(eventLoopGroup,http,K8sAPI.V1, RESOURCE,LIST_TYPE,EVENT_TYPE);
	}

	@Override
	public NamespacedSecrets namespace(String namespace) {
		return new DefaultNamespacedSecrets(eventLoopGroup(),http(),K8sAPI.V1,RESOURCE,LIST_TYPE,EVENT_TYPE,namespace);
	}

	@Override
	public SecretBuilder builder(String namespace) {
		return new DefaultSecretBuilder(http(), K8sAPI.V1, namespace, RESOURCE);
	}

}