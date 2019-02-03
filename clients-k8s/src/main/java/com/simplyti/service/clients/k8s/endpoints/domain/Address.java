package com.simplyti.service.clients.k8s.endpoints.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Address {
	
	private final String ip;
	
	@JsonCreator
	public Address(
			@JsonProperty("ip")String ip) {
		this.ip=ip;
	}

}

