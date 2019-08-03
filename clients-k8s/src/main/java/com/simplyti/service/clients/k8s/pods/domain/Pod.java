package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Pod extends K8sResource {
	
	private final PodSpec spec;
	private final PodStatus status;

	@CompiledJson
	public Pod(
			String kind,
			String apiVersion,
			Metadata metadata,
			PodSpec spec,
			PodStatus status) {
		super(kind,apiVersion,metadata);
		this.spec=spec;
		this.status=status;
	}

}
