package com.simplyti.service.clients.k8s.secrets.domain;

import java.util.Map;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Secret extends K8sResource {
	
	private final Map<String, SecretData> data;
	private final String type;
	
	@JsonCreator
	public Secret(
			@JsonProperty("kind") String kind,
			@JsonProperty("apiVersion") String apiVersion,
			@JsonProperty("metadata") Metadata metadata,
			@JsonProperty("data") Map<String,SecretData> data,
			@JsonProperty("type") String type) {
		super(kind,apiVersion,metadata);
		this.data=data;
		this.type=type;
	}

}