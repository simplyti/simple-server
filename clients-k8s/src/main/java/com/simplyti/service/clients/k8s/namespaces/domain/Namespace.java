package com.simplyti.service.clients.k8s.namespaces.domain;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

public class Namespace extends K8sResource{
	
	@CompiledJson
	public Namespace(
			String kind,
			String apiVersion,
			Metadata metadata) {
		super(kind,apiVersion,metadata);
	}

}
