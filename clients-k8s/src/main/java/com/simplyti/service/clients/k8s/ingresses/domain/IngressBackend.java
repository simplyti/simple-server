package com.simplyti.service.clients.k8s.ingresses.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressBackend {
	
	private final String serviceName;
	private final Object servicePort;
	
	@JsonCreator
	public IngressBackend(
			@JsonProperty("serviceName") String serviceName,
			@JsonProperty("servicePort") Object servicePort) {
		this.serviceName=serviceName;
		this.servicePort=servicePort;
	}

}
