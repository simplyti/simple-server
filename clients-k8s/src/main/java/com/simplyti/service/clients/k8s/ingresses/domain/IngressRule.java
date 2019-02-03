package com.simplyti.service.clients.k8s.ingresses.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressRule {
	
	private final String host;
	private final IngressHttp http;
	
	@JsonCreator
	public IngressRule(
			@JsonProperty("host") String host,
			@JsonProperty("http") IngressHttp http) {
		this.host=host;
		this.http=http;
	}

}
