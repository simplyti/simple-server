package com.simplyti.service.security.oidc.handler;

import java.security.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class DefaultOpenIdHandler implements OpenIdHandler{
	
	private final Key signKey;
	
	public DefaultOpenIdHandler(Key key) {
		this.signKey=key;
	}

	@Override
	public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, Claims claims) {
		return signKey;
	}

	@Override
	public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, String plaintext) {
		return signKey;
	}

}
