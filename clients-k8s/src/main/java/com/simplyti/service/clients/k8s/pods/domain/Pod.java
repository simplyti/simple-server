package com.simplyti.service.clients.k8s.pods.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

public class Pod extends K8sResource {
	
	@JsonCreator
	public Pod(
			@JsonProperty("kind") String kind,
			@JsonProperty("apiVersion") String apiVersion,
			@JsonProperty("metadata") Metadata metadata) {
		super(kind,apiVersion,metadata);
	}

}
