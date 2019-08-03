package com.simplyti.service.clients.k8s.endpoints.domain;

import java.util.List;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Subset {
	
	private final List<Address> addresses;
	private final List<Port> ports;
	
	@CompiledJson
	public Subset(
			List<Address> addresses,
			List<Port> ports) {
		this.addresses=addresses;
		this.ports=ports;
	}

}
