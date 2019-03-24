package com.simplyti.service.clients.k8s.services.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class ServiceLoadBalancerIngress {
	
	private final String ip;
	private final String hostname;

	@JsonCreator
	public ServiceLoadBalancerIngress(
			@JsonProperty("ip") String ip,
			@JsonProperty("hostname") String hostname) {
		this.ip=ip;
		this.hostname=hostname;
	}

}
