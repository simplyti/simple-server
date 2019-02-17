package com.simplyti.service.discovery.k8s;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent=true)
public class KubernetesDiscoveryConfig {
	
	private final String apiServer;

}
