package com.simplyti.service.clients.k8s.endpoints.domain;

import java.util.List;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Subset {
	
	private final List<Address> addresses;
	private final List<Port> ports;
	
	@JsonCreator
	public Subset(
			@JsonProperty("addresses") List<Address> addresses,
			@JsonProperty("ports") List<Port> ports) {
		this.addresses=addresses;
		this.ports=ports;
	}

}
