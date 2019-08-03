package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.simplyti.service.clients.k8s.json.coder.PortConverter;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class HttpProbe {
	
	private final String path;
	private final Object port;
	
	@CompiledJson
	public HttpProbe(
			String path,
			@JsonAttribute(converter=PortConverter.class) Object port) {
		this.path=path;
		this.port=port;
	}

}
