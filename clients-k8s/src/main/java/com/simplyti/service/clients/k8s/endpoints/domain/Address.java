package com.simplyti.service.clients.k8s.endpoints.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Address {
	
	private final String ip;
	
	@CompiledJson
	public Address(
			String ip) {
		this.ip=ip;
	}

}

