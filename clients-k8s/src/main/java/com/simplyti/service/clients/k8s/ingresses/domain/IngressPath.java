package com.simplyti.service.clients.k8s.ingresses.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressPath {
	
	private final String path;
	private final IngressBackend backend;
	
	@JsonCreator
	public IngressPath(
			@JsonProperty("path") String path,
			@JsonProperty("backend") IngressBackend backend) {
		this.path=path;
		this.backend=backend;
	}

}
