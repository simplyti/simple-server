package com.simplyti.service.clients.k8s;

import java.time.LocalDateTime;

import com.dslplatform.json.Configuration;
import com.dslplatform.json.DslJson;
import com.simplyti.service.clients.k8s.json.coder.LocalDateTimeDecoder;

public class K8sDomainConfiguration implements Configuration {

	@Override
	@SuppressWarnings("unchecked")
	public void configure(@SuppressWarnings("rawtypes") DslJson json) {
		json.registerReader(LocalDateTime.class, new LocalDateTimeDecoder());
	}

}
