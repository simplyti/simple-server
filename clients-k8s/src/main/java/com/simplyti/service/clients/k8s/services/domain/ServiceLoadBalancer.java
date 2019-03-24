package com.simplyti.service.clients.k8s.services.domain;

import java.util.List;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class ServiceLoadBalancer {
	
	private final List<ServiceLoadBalancerIngress> ingress;

	@JsonCreator
	public ServiceLoadBalancer(
			@JsonProperty("ingress") List<ServiceLoadBalancerIngress> ingress) {
		this.ingress=ingress;
	}

}
