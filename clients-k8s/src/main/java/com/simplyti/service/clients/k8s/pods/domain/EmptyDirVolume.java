package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class EmptyDirVolume  {

	private final String medium;

	@CompiledJson
	public EmptyDirVolume(String medium) {
		this.medium=medium;
	}

}
