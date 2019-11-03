package com.simplyti.service.security.oidc.handler;

import java.security.Key;
import java.util.Optional;

import javax.inject.Inject;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.security.oidc.config.auto.AutodiscoveryOpenIdIncompleteException;
import com.simplyti.service.security.oidc.config.auto.FullAutodiscoveredOpenIdConfig;
import com.simplyti.service.security.oidc.config.auto.WellKnownOpenIdResponse;
import com.simplyti.service.security.oidc.jwk.JsonWebKey;
import com.simplyti.service.security.oidc.jwk.JsonWebKeys;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class AutodiscoveredOpenIdHandler extends DefaultFullOpenidHandler implements FullOpenidHandler{
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(AutodiscoveredOpenIdHandler.class);
	
	private WellKnownOpenIdResponse oidc;
	private JsonWebKeys keys;
	private final Json json;
	
	@Inject
	public AutodiscoveredOpenIdHandler(HttpClient client,FullAutodiscoveredOpenIdConfig openId, Json json) {
		super(new FullOpenidHandlerConfig(null,null,null,openId.callbackUri(),openId.clientId(),openId.clientSecret(),openId.cipherKey()),json);
		getWellKnownConfiguration(client,openId.endpoint());
		this.json=json;
	}

	private void getWellKnownConfiguration(HttpClient client, Endpoint endpoint) {
		Future<WellKnownOpenIdResponse> futureResponse = client.request()
				.withEndpoint(endpoint)
				.withCheckStatusCode()
				.get("/.well-known/openid-configuration")
				.fullResponse(response->json.deserialize(response.content(), WellKnownOpenIdResponse.class));
			futureResponse.addListener(f->{
				if(f.isSuccess()) {
					this.oidc = futureResponse.getNow();
					getJwsKey(client);
				} else {
					log.warn(f.cause());
				}
			});
	}
	
	private void getJwsKey(HttpClient client) {
		HttpEndpoint endpoint = HttpEndpoint.of(oidc.jwks_uri());
		Future<JsonWebKeys> futureResponse = client.request()
			.withEndpoint(endpoint )
			.withCheckStatusCode()
			.get(endpoint.path())
			.fullResponse(response->json.deserialize(response.content(), JsonWebKeys.class));
		futureResponse.addListener(f->{
			if(f.isSuccess()) {
				this.keys = futureResponse.getNow();
			} else {
				log.warn(f.cause());
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
		return oidc.authorization_endpoint();
	}
	
	protected String tokenEndpoint() {
		return oidc.token_endpoint();
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
