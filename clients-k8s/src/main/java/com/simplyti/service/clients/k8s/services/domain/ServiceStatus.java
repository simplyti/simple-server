package com.simplyti.service.clients.k8s.services.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class ServiceStatus {
	
	private final ServiceLoadBalancer loadBalancer;

	@JsonCreator
	public ServiceStatus(
			@JsonProperty("loadBalancer") ServiceLoadBalancer loadBalancer) {
		this.loadBalancer=loadBalancer;
	}

}
