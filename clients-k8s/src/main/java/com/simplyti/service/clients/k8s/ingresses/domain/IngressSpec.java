package com.simplyti.service.clients.k8s.ingresses.domain;

import java.util.List;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressSpec {
	
	private final List<IngressRule> rules;
	private final List<IngressTls> tls;

	@JsonCreator
	public IngressSpec(
			@JsonProperty("rules") List<IngressRule> rules,
			@JsonProperty("tls") List<IngressTls> tls) {
		this.rules=rules;
		this.tls=tls;
	}

}
