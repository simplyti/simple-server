package com.simplyti.service.clients.k8s.services.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class ServiceStatus {
	
	private final ServiceLoadBalancer loadBalancer;

	@CompiledJson
	public ServiceStatus(ServiceLoadBalancer loadBalancer) {
		this.loadBalancer=loadBalancer;
	}

}
