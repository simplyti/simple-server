package com.simplyti.service.clients.k8s.ingresses.domain;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Ingress extends K8sResource {
	
	private final IngressSpec spec;
	
	@CompiledJson
	public Ingress(
			String kind,
			String apiVersion,
			Metadata metadata,
			IngressSpec spec) {
		super(kind,apiVersion,metadata);
		this.spec=spec;
	}

}
