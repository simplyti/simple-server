package com.simplyti.service.clients.k8s.serviceaccounts.domain;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ServiceAccount extends K8sResource {
	
	
	@CompiledJson
	public ServiceAccount(
			String kind,
			String apiVersion,
			Metadata metadata) {
		super(kind,apiVersion,metadata);
	}

}