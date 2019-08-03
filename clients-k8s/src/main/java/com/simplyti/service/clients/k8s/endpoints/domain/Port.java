package com.simplyti.service.clients.k8s.endpoints.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Port {
	
	private final String name;
	private final Integer port;
	private final String protocol;
	
	@CompiledJson
	public Port(
			String name,
			Integer port,
			String protocol) {
		this.name=name;
		this.port=port;
		this.protocol=protocol;
	}

}
