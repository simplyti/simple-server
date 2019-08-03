package com.simplyti.service.clients.k8s.ingresses.domain;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.simplyti.service.clients.k8s.json.coder.PortConverter;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IngressBackend {
	
	private final String serviceName;
	private final Object servicePort;
	
	@CompiledJson
	public IngressBackend(
			String serviceName,
			@JsonAttribute(converter=PortConverter.class) Object servicePort) {
		this.serviceName=serviceName;
		this.servicePort=servicePort;
	}

}
