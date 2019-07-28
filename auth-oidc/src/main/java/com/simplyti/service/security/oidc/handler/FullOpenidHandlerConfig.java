package com.simplyti.service.security.oidc.handler;

import java.security.Key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class FullOpenidHandlerConfig {
	
	private final Key key;
	private final String authorizationEndpoint;
	private final String tokenEndpoint;
	private final String callbackUri;
	private final String clientId;
	private final String clientSecret;
	private final String cipherKey;

}
