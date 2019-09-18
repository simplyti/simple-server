package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ContainerStatus {
	
	private final Boolean ready;
	private final ContainerState state;
	private final String name;

	@CompiledJson
	public ContainerStatus(Boolean ready,ContainerState state,String name) {
		this.ready=ready;
		this.state=state;
		this.name=name;
	}

}
