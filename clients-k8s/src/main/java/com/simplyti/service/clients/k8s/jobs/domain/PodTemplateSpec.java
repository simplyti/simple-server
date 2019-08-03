package com.simplyti.service.clients.k8s.jobs.domain;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.pods.domain.PodSpec;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class PodTemplateSpec {
	
	private final PodSpec spec;

	@CompiledJson
	public PodTemplateSpec(PodSpec spec) {
		this.spec=spec;
	}

}
