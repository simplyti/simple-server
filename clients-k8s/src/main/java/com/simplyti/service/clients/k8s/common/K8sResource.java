package com.simplyti.service.clients.k8s.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public abstract class K8sResource {
	
	private final String kind;
	private final String apiVersion;
	private final Metadata metadata;

}
