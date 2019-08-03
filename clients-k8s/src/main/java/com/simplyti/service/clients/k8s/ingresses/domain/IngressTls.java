package com.simplyti.service.clients.k8s.ingresses.domain;

import java.util.List;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressTls {
	
	private final List<String> hosts;
	private final String secretName;

	@CompiledJson
	public IngressTls(
			List<String> hosts,
			String secretName) {
		this.hosts=hosts;
		this.secretName=secretName;
	}

}
