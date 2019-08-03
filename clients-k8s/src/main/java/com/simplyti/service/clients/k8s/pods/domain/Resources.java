package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Resources {
	
	private final ResourcesSpec limits;
	private final ResourcesSpec requests;

	@CompiledJson
	public Resources(
			ResourcesSpec limits,
			ResourcesSpec requests) {
		this.limits=limits;
		this.requests=requests;
	}

}
