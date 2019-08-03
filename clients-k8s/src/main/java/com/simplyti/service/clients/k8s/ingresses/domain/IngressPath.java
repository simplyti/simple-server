package com.simplyti.service.clients.k8s.ingresses.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressPath {
	
	private final String path;
	private final IngressBackend backend;
	
	@CompiledJson
	public IngressPath(
			String path,
			IngressBackend backend) {
		this.path=path;
		this.backend=backend;
	}

}
