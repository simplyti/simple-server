package com.simplyti.service.clients.k8s.ingresses.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressServiceBackend {
	
	private final String name;
	private final ServiceBackendPort port;
	
	@CompiledJson
	public IngressServiceBackend(String name, ServiceBackendPort port) {
		this.name=name;
		this.port=port;
	}

}
