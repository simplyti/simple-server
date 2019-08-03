package com.simplyti.service.clients.k8s.ingresses.domain;

import java.util.List;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressHttp {
	
private final List<IngressPath> paths;
	
	@CompiledJson
	public IngressHttp(List<IngressPath> paths) {
		this.paths=paths;
	}

}
