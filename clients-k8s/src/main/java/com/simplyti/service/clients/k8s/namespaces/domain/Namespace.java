package com.simplyti.service.clients.k8s.namespaces.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

public class Namespace extends K8sResource{
	
	@JsonCreator
	public Namespace(
			@JsonProperty("kind") String kind,
			@JsonProperty("apiVersion") String apiVersion,
			@JsonProperty("metadata") Metadata metadata) {
		super(kind,apiVersion,metadata);
	}

}
