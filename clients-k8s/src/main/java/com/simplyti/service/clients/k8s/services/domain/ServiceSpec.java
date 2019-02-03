package com.simplyti.service.clients.k8s.services.domain;

import java.util.List;
import java.util.Map;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class ServiceSpec {
	
	private final String clusterIP;
	private final String loadBalancerIP;
	private final List<ServicePort> ports;
	private final Map<String,String> selector;
	private final ServiceType type;
	
	@JsonCreator
	public ServiceSpec(
			@JsonProperty("clusterIP") String clusterIP,
			@JsonProperty("loadBalancerIP") String loadBalancerIP,
			@JsonProperty("ports") List<ServicePort> ports,
			@JsonProperty("selector") Map<String,String> selector,
			@JsonProperty("type") ServiceType type) {
		this.clusterIP=clusterIP;
		this.loadBalancerIP=loadBalancerIP;
		this.ports=ports;
		this.selector=selector;
		this.type=type;
	}

}
