package com.simplyti.service.security.oidc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class OpenIdCallbackConfig {
	
	private final String callbackUri;
	private final SecretKey cipherKey;
	
	public OpenIdCallbackConfig(String callbackUri, SecretKey cipherKey) {
		this.callbackUri=callbackUri;
		this.cipherKey=cipherKey;
	}
	
	public OpenIdCallbackConfig(String callbackUri, String cipherKey) {
		this.callbackUri=callbackUri;
		
		byte[] keyBytes = new byte[16];
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
        md.update(cipherKey.getBytes());
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
        this.cipherKey = new SecretKeySpec(keyBytes, "AES");
	}

}
