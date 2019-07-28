package com.simplyti.service.security.oidc.callback;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
@Getter
public class State {
	
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;
	private final String tokenEndpoint;

	@CompiledJson
	public State(String clientId,String clientSecret,String redirectUri,String tokenEndpoint) {
		this.clientId=clientId;
		this.clientSecret=clientSecret;
		this.redirectUri=redirectUri;
		this.tokenEndpoint=tokenEndpoint;
	}
}
