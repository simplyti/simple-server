package com.simplyti.service.clients.k8s.services.domain;

import java.util.List;

import com.dslplatform.json.CompiledJson;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class ServiceLoadBalancer {
	
	private final List<ServiceLoadBalancerIngress> ingress;

	@CompiledJson
	public ServiceLoadBalancer(List<ServiceLoadBalancerIngress> ingress) {
		this.ingress=ingress;
	}

}
