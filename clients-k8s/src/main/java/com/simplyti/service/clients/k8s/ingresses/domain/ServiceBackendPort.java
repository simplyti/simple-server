package com.simplyti.service.clients.k8s.ingresses.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ServiceBackendPort {
	
	private final String name;
	private final Integer number;
	
	@CompiledJson
	public ServiceBackendPort(String name, Integer number) {
		this.name=name;
		this.number=number;
	}

}
