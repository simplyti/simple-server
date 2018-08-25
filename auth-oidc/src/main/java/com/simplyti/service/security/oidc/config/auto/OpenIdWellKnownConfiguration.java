package com.simplyti.service.security.oidc.config.auto;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class OpenIdWellKnownConfiguration {
	
	private final String tokenEndpoint;
	private final String authorizationEndpoint;
	private final String jwsUri;

	@JsonCreator
	public OpenIdWellKnownConfiguration(
			@JsonProperty("token_endpoint") String tokenEndpoint,
			@JsonProperty("authorization_endpoint") String authorizationEndpoint,
			@JsonProperty("jwks_uri") String jwsUri) {
		this.tokenEndpoint=tokenEndpoint;
		this.authorizationEndpoint=authorizationEndpoint;
		this.jwsUri=jwsUri;
	}

}
