package com.simplyti.service.clients.k8s.services.domain;

import java.util.Map;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class ServicePort {
	
	private final String name;
	private final Integer port;
	private final ServiceProtocol protocol;
	private final Map<String,String> selector;
	private final Object targetPort;
	
	@JsonCreator
	public ServicePort(
			@JsonProperty("name") String name,
			@JsonProperty("port") Integer port,
			@JsonProperty("protocol") ServiceProtocol protocol,
			@JsonProperty("selector") Map<String,String> selector,
			@JsonProperty("targetPort") Object targetPort) {
		this.name=name;
		this.port=port;
		this.protocol=protocol;
		this.selector=selector;
		this.targetPort=targetPort;
	}

}
