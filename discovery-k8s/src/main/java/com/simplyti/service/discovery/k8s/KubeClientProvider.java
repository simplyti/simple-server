package com.simplyti.service.discovery.k8s;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.clients.k8s.KubeClient;

import io.netty.channel.EventLoopGroup;

public class KubeClientProvider implements Provider<KubeClient>{
	
	private final EventLoopGroup eventLoopGroup;
	private final KubernetesDiscoveryConfig config;
	
	@Inject
	public KubeClientProvider(EventLoopGroup eventLoopGroup,KubernetesDiscoveryConfig config) {
		this.eventLoopGroup=eventLoopGroup;
		this.config=config;
	}
	
	
	@Override
	public KubeClient get() {
		return KubeClient.builder()
				.server(config.apiServer())
				.eventLoopGroup(eventLoopGroup)
				.build();
	}

}