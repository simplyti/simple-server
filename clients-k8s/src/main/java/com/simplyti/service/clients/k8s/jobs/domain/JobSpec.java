package com.simplyti.service.clients.k8s.jobs.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class JobSpec {
	
	private final PodTemplateSpec template;

	@CompiledJson
	public JobSpec(PodTemplateSpec template) {
		this.template=template;
	}

}
