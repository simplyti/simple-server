package com.simplyti.service.clients.k8s.services.domain;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Service extends K8sResource {
	
	private final ServiceSpec spec;
	private final ServiceStatus status;

	@CompiledJson
	public Service(
			String kind,
			String apiVersion,
			Metadata metadata,
			ServiceSpec spec,
			ServiceStatus status) {
		super(kind,apiVersion,metadata);
		this.spec=spec;
		this.status=status;
	}

}
