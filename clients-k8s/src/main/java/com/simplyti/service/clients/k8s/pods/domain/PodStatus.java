package com.simplyti.service.clients.k8s.pods.domain;

import java.util.List;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class PodStatus {
	
	private final String podIP;
	private List<ContainerStatus> containerStatuses;

	@CompiledJson
	public PodStatus(
			String podIP,
			List<ContainerStatus> containerStatuses) {
		this.podIP=podIP;
		this.containerStatuses=containerStatuses;
	}

}