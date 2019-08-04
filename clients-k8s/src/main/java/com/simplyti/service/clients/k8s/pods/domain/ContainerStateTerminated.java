package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ContainerStateTerminated {
	
	private final int exitCode;
	
	@CompiledJson
	public ContainerStateTerminated(int exitCode) {
		this.exitCode=exitCode;
	}

}
