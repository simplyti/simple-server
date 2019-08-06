package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class EnvironmentVariable {
	
	private final String name;
	private final String value;
	
	@CompiledJson
	public EnvironmentVariable(String name, String value) {
		this.name=name;
		this.value=value;
	}

}
