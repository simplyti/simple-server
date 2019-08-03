package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ContainerStatus {
	
	private Boolean ready;

	@CompiledJson
	public ContainerStatus(Boolean ready) {
		this.ready=ready;
	}

}
