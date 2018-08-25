package com.simplyti.service.security.oidc.config.auto;

import com.simplyti.service.clients.http.HttpEndpoint;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class DefaultFullAutodiscoveredOpenIdConfig implements FullAutodiscoveredOpenIdConfig {

	private final String callbackUri;
	private final String cipherKey;
	private final HttpEndpoint endpoint;
	private final String clientId;
	private final String clientSecret;

	public DefaultFullAutodiscoveredOpenIdConfig(String openIdProvider, String callbackUri, String clientId,String clientSecret,String cipherKey) {
		this.endpoint=HttpEndpoint.of(openIdProvider);
		this.callbackUri=callbackUri;
		this.cipherKey=cipherKey;
		this.clientId=clientId;
		this.clientSecret=clientSecret;
	}

}
