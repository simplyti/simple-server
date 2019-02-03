package com.simplyti.service.clients.k8s.endpoints.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Port {
	
	private final String name;
	private final Integer port;
	private final String protocol;
	
	@JsonCreator
	public Port(
			@JsonProperty("name")String name,
			@JsonProperty("port")Integer port,
			@JsonProperty("protocol")String protocol) {
		this.name=name;
		this.port=port;
		this.protocol=protocol;
	}

}
