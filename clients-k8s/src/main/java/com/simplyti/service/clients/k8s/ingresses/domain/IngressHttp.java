package com.simplyti.service.clients.k8s.ingresses.domain;

import java.util.List;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressHttp {
	
private final List<IngressPath> paths;
	
	@JsonCreator
	public IngressHttp(
			@JsonProperty("paths") List<IngressPath> paths) {
		this.paths=paths;
	}

}
