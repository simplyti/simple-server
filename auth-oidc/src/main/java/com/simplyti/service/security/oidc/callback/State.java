package com.simplyti.service.security.oidc.callback;

import com.dslplatform.json.CompiledJson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
@Getter
@AllArgsConstructor(onConstructor=@__(@CompiledJson))
public class State {
	
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;
	private final String tokenEndpoint;

}
