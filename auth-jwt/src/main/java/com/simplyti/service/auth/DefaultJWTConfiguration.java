package com.simplyti.service.auth;

import java.security.Key;

public class DefaultJWTConfiguration implements JWTConfiguration {

	private Key key;

	public DefaultJWTConfiguration(Key key) {
		this.key=key;
	}

	@Override
	public Key key() {
		return key;
	}

}
