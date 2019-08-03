package com.simplyti.service.clients.k8s.jobs.domain;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Job extends K8sResource {
	
	private final JobSpec spec;
	private final JobStatus status;
	
	@CompiledJson
	public Job(
			String kind,
			String apiVersion,
			Metadata metadata,
			JobSpec spec,
			JobStatus status) {
		super(kind,apiVersion,metadata);
		this.spec=spec;
		this.status=status;
	}

}
