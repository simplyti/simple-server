package com.simplyti.service.security.oidc.handler;

import java.security.Key;
import java.util.Optional;

import javax.inject.Inject;

import com.jsoniter.JsonIterator;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.security.oidc.config.auto.AutodiscoveryOpenIdIncompleteException;
import com.simplyti.service.security.oidc.config.auto.FullAutodiscoveredOpenIdConfig;
import com.simplyti.service.security.oidc.config.auto.OpenIdWellKnownConfiguration;
import com.simplyti.service.security.oidc.jwk.JsonWebKey;
import com.simplyti.service.security.oidc.jwk.JsonWebKeys;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.Future;

public class AutodiscoveredOpenIdHandler extends DefaultFullOpenidHandler implements FullOpenidHandler{
	
	private OpenIdWellKnownConfiguration oidc;
	private JsonWebKeys keys;
	
	@Inject
	public AutodiscoveredOpenIdHandler(HttpClient client,FullAutodiscoveredOpenIdConfig openId) {
		super(null,null,null,openId.callbackUri(),openId.clientId(),openId.clientSecret(),openId.cipherKey());
		getWellKnownConfiguration(client,openId.endpoint());
	}

	private void getWellKnownConfiguration(HttpClient client, Endpoint endpoint) {
		Future<FullHttpResponse> futureResponse = client.withEndpoin(endpoint)
				.withCheckStatusCode()
				.get("/.well-known/openid-configuration")
				.fullResponse();
			futureResponse.addListener(f->{
				if(f.isSuccess()) {
					FullHttpResponse response = futureResponse.getNow();
					byte[] data = new byte[response.content().readableBytes()];
					response.content().readBytes(data);
					response.release();
					this.oidc = JsonIterator.deserialize(data, OpenIdWellKnownConfiguration.class);
					getJwsKey(client);
				} else {
					// TODO: retry?
				}
			});
	}
	
	private void getJwsKey(HttpClient client) {
		HttpEndpoint endpoint = HttpEndpoint.of(oidc.jwsUri());
		Future<FullHttpResponse> futureResponse = client.withEndpoin(endpoint )
			.withCheckStatusCode()
			.get(endpoint.path())
			.fullResponse();
		futureResponse.addListener(f->{
			if(f.isSuccess()) {
				FullHttpResponse response = futureResponse.getNow();
				byte[] data = new byte[response.content().readableBytes()];
				response.content().readBytes(data);
				response.release();
				this.keys = JsonIterator.deserialize(data, JsonWebKeys.class);
				keys.keys();
			} else {
				// TODO: retry?
			}
		});
	}

	@Override
	public String getAuthorizationUrl(HttpRequest request) {
		if(oidc==null) {
			return null;
		}else {
			return super.getAuthorizationUrl(request);
		}
	}
	
	protected String authorizationEndpoint() {
		return oidc.authorizationEndpoint();
	}
	
	protected String tokenEndpoint() {
		return oidc.tokenEndpoint();
	}

	@Override
	public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, Claims claims) {
		if(keys==null) {
			throw new AutodiscoveryOpenIdIncompleteException();
		}else {
			Optional<JsonWebKey> optional = keys.keys().stream().filter(key->key.kid().equals(header.get("kid"))).findFirst();
			if(optional.isPresent()) {
				return optional.get().key();
			}else {
				return null;
			}
		}
	}

	@Override
	public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, String plaintext) {
		return null;
	}

}
