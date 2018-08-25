package com.simplyti.service.security.oidc.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class DefaultOpenIdClientConfig implements OpenIdClientConfig {
	
	private final String clientId;
	private final String clientSecret;

}
