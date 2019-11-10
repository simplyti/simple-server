package com.simplyti.service.security.oidc.config;

import com.simplyti.service.clients.http.HttpEndpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class WellKnownOpenIdConfig {
	
	private final HttpEndpoint endpoint;

}
