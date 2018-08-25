package com.simplyti.service.security.oidc.handler;

import io.netty.handler.codec.http.HttpRequest;

public interface RedirectableOpenIdHandler extends OpenIdHandler{
	
	public default boolean isRedirectable() {
		return true;
	}

	public String getAuthorizationUrl(HttpRequest request);

}
