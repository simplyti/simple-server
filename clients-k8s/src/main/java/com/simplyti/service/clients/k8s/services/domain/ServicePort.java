package com.simplyti.service.clients.k8s.services.domain;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.simplyti.service.clients.k8s.json.coder.PortConverter;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class ServicePort {
	
	private final String name;
	private final Integer port;
	private final ServiceProtocol protocol;
	private final Object targetPort;
	
	@CompiledJson
	public ServicePort(
			String name,
			Integer port,
			ServiceProtocol protocol,
			@JsonAttribute(converter=PortConverter.class) Object targetPort) {
		this.name=name;
		this.port=port;
		this.protocol=protocol;
		this.targetPort=targetPort;
	}

}
