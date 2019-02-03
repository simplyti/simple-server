package com.simplyti.service.clients.k8s.endpoints.domain;

import java.util.Collection;
import java.util.List;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Endpoint extends K8sResource {
	
	private final Collection<Subset> subsets;
	
	@JsonCreator
	public Endpoint(
			@JsonProperty("kind") String kind,
			@JsonProperty("apiVersion") String apiVersion,
			@JsonProperty("metadata") Metadata metadata,
			@JsonProperty("subsets") List<Subset> subsets) {
		super(kind,apiVersion,metadata);
		this.subsets=subsets;
	}

}