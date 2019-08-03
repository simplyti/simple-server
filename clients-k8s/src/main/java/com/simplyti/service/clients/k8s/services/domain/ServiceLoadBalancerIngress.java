package com.simplyti.service.clients.k8s.services.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class ServiceLoadBalancerIngress {
	
	private final String ip;
	private final String hostname;

	@CompiledJson
	public ServiceLoadBalancerIngress(
			String ip,
			String hostname) {
		this.ip=ip;
		this.hostname=hostname;
	}

}
