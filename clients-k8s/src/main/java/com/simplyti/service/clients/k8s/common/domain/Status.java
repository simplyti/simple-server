package com.simplyti.service.clients.k8s.common.domain;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Status extends K8sResource{
	
	private final String message;

	@CompiledJson
	public Status(
			String kind,
			String apiVersion,
			Metadata metadata,
			String message) {
		super(kind,apiVersion,metadata);
		this.message=message;
	}

}
