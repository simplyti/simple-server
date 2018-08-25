package com.simplyti.service.security.oidc.handler;

import io.jsonwebtoken.SigningKeyResolver;

public interface OpenIdHandler extends SigningKeyResolver{

	public default boolean isRedirectable() {
		return false;
	}
	
	public default boolean isFullOpenId() {
		return false;
	}

	public default RedirectableOpenIdHandler redirectable() {
		return (RedirectableOpenIdHandler) this;
	}

}
