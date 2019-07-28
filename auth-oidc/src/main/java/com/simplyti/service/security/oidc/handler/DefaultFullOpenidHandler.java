package com.simplyti.service.security.oidc.handler;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import com.google.common.collect.Maps;
import com.simplyti.service.api.serializer.json.Json;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
public class DefaultFullOpenidHandler extends DefaultRedirectableOpenIdHandler implements FullOpenidHandler {

	private final String tokenEndpoint;
	
	@Getter
	private final String clientSecret;
	@Getter
	private final SecretKey cipherKey;
	
	private final Json json;

	@Inject
	public DefaultFullOpenidHandler(FullOpenidHandlerConfig config,Json json) {
		super(config.key(), config.authorizationEndpoint(), config.callbackUri(), config.clientId());
		this.tokenEndpoint=config.tokenEndpoint();
		this.clientSecret=config.clientSecret();
		this.json=json;
		
		byte[] keyBytes = new byte[16];
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
        md.update(config.cipherKey().getBytes());
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
        this.cipherKey = new SecretKeySpec(keyBytes, "AES");
	}
	
	@Override
	protected String callbackUri(HttpRequest request) {
		return "https://"+request.headers().get(HttpHeaderNames.HOST)+super.callbackUri(request);
	}
	
	protected String state(HttpRequest request) {
		Map<String, String> state = Maps.newHashMap();
		state.put("redirectUri", "https://"+request.headers().get(HttpHeaderNames.HOST)+request.uri());
		state.put("tokenEndpoint", tokenEndpoint());
		state.put("clientId", clientId());
		state.put("clientSecret", clientSecret());
		
		try {
			Cipher ci = Cipher.getInstance("AES");
			ci.init(Cipher.ENCRYPT_MODE, cipherKey);
			byte[] encripted = ci.doFinal(json.serialize(state));
			return Base64.getEncoder().encodeToString(encripted);
		}catch(InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			throw new IllegalStateException(e);
		}
	}

	protected String tokenEndpoint() {
		return tokenEndpoint;
	}

}
