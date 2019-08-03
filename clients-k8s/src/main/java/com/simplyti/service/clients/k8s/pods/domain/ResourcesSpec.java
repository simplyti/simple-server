package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ResourcesSpec {

	private final String memory;
	private final String cpu;

	@CompiledJson
	public ResourcesSpec(
			String memory,
			String cpu) {
		this.memory=memory;
		this.cpu=cpu;
	}
}
