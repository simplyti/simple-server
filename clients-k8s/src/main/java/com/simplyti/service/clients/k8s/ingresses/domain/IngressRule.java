package com.simplyti.service.clients.k8s.ingresses.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressRule {
	
	private final String host;
	private final IngressHttp http;
	
	@CompiledJson
	public IngressRule(
			String host,
			IngressHttp http) {
		this.host=host;
		this.http=http;
	}

}
