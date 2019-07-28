package com.simplyti.service.security.oidc.config.auto;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class OpenIdWellKnownConfiguration {
	
	private final String token_endpoint;
	private final String authorization_endpoint;
	private final String jwks_uri;

	@CompiledJson
	public OpenIdWellKnownConfiguration(
			String token_endpoint,
			String authorization_endpoint,
			String jwks_uri) {
		this.token_endpoint=token_endpoint;
		this.authorization_endpoint=authorization_endpoint;
		this.jwks_uri=jwks_uri;
	}

}
