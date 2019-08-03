package com.simplyti.service.clients.k8s.common.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Status {
	
	private final String message;

	@CompiledJson
	public Status(String message) {
		this.message=message;
	}

}
