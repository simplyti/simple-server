package com.simplyti.service.clients.k8s.ingresses.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Ingress extends K8sResource {
	
	private final IngressSpec spec;
	
	@JsonCreator
	public Ingress(
			@JsonProperty("kind") String kind,
			@JsonProperty("apiVersion") String apiVersion,
			@JsonProperty("metadata") Metadata metadata,
			@JsonProperty("spec") IngressSpec spec) {
		super(kind,apiVersion,metadata);
		this.spec=spec;
	}

}
