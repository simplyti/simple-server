package com.simplyti.service.discovery.k8s.oidc;

import java.util.Map;

import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.security.oidc.config.OpenIdClientConfig;
import com.simplyti.service.security.oidc.config.auto.FullAutodiscoveredOpenIdConfig;
import com.simplyti.service.security.oidc.handler.AutodiscoveredOpenIdHandler;

public class K8sAutodiscoveredOpenIdHandler extends AutodiscoveredOpenIdHandler{

	private final Map<String, OpenIdClientConfig> openIdClientSecrets;
	private final String authSecret;

	public K8sAutodiscoveredOpenIdHandler(HttpClient client, Map<String, OpenIdClientConfig> openIdClientSecrets,String namespace, String authSecret, FullAutodiscoveredOpenIdConfig openId) {
		super(client, openId);
		this.openIdClientSecrets=openIdClientSecrets;
		this.authSecret=String.join(":", namespace,authSecret);
	}
	
	@Override
	public String clientId() {
		OpenIdClientConfig client = openIdClientSecrets.get(authSecret);
		if(client==null) {
			return null;
		}else {
			return client.clientId();
		}
	}
	
	@Override
	public String clientSecret() {
		return openIdClientSecrets.get(authSecret).clientSecret();
	}

}
