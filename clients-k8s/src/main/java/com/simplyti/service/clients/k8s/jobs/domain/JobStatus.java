package com.simplyti.service.clients.k8s.jobs.domain;


import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class JobStatus {
	
	private final Integer active;
	private final Integer failed;
	private final Integer succeeded;

	@CompiledJson
	public JobStatus(Integer active,Integer failed,Integer succeeded) {
		this.active=active;
		this.failed=failed;
		this.succeeded=succeeded;
	}

}
