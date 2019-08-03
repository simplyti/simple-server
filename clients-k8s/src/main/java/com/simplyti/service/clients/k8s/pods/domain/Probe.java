package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Probe {
	
	private final HttpProbe httpGet;
	
	@CompiledJson
	public Probe(HttpProbe httpGet) {
		this.httpGet=httpGet;
	}

}
