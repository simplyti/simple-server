package com.simplyti.service.security.oidc.handler;

import javax.crypto.SecretKey;

public interface FullOpenidHandler extends RedirectableOpenIdHandler {
	
	public default boolean isFullOpenId() {
		return true;
	}

	String callbackUri();

	SecretKey cipherKey();

}
