package com.simplyti.service.clients.k8s.secrets.domain;

import java.util.Map;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Secret extends K8sResource {
	
	private final Map<String, SecretData> data;
	private final String type;
	
	@CompiledJson
	public Secret(
			String kind,
			String apiVersion,
			Metadata metadata,
			Map<String,SecretData> data,
			String type) {
		super(kind,apiVersion,metadata);
		this.data=data;
		this.type=type;
	}

}