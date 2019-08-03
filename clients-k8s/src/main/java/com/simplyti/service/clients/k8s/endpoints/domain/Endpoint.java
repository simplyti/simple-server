package com.simplyti.service.clients.k8s.endpoints.domain;

import java.util.Collection;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Endpoint extends K8sResource {
	
	private final Collection<Subset> subsets;
	
	@CompiledJson
	public Endpoint(
			String kind,
			String apiVersion,
			Metadata metadata,
			Collection<Subset> subsets) {
		super(kind,apiVersion,metadata);
		this.subsets=subsets;
	}

}