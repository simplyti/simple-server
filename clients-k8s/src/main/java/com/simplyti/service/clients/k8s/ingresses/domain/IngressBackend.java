package com.simplyti.service.clients.k8s.ingresses.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressBackend {
	
	private final IngressServiceBackend service;
	
	@CompiledJson
	public IngressBackend(IngressServiceBackend service) {
		this.service=service;
	}

}
