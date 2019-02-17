package com.simplyti.service.discovery.k8s.oidc;

import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.security.oidc.config.auto.FullAutodiscoveredOpenIdConfig;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class K8sAutodiscoveredOpenIdProviderConfig implements FullAutodiscoveredOpenIdConfig{

	private final HttpEndpoint endpoint;
	private final String callbackUri;
	private final String cipherKey;
	
	private String clientId;
	private String clientSecret;

	public K8sAutodiscoveredOpenIdProviderConfig(String endpoint, String callbackUri, String cipherKey) {
		this.endpoint=HttpEndpoint.of(endpoint);
		this.callbackUri=callbackUri;
		this.cipherKey=cipherKey;
	}

}
