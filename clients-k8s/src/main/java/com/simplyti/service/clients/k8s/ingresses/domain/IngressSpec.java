package com.simplyti.service.clients.k8s.ingresses.domain;

import java.util.List;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressSpec {
	
	private final List<IngressRule> rules;
	private final List<IngressTls> tls;

	@CompiledJson
	public IngressSpec(
			List<IngressRule> rules,
			List<IngressTls> tls) {
		this.rules=rules;
		this.tls=tls;
	}

}
