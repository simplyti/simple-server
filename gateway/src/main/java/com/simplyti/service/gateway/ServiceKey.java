package com.simplyti.service.gateway;

import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@EqualsAndHashCode
@AllArgsConstructor
public class ServiceKey {
	
	private final String host;
	private final HttpMethod method;
	private final String path;

}
