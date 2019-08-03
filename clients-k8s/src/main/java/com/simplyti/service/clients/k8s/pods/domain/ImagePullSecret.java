package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ImagePullSecret {
	
	private final String name;

	@CompiledJson
	public ImagePullSecret(String name) {
		this.name=name;
	}

}