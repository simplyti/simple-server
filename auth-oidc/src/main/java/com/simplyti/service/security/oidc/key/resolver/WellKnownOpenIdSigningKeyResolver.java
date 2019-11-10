package com.simplyti.service.security.oidc.key.resolver;

import java.security.Key;
import java.util.Optional;

import javax.inject.Inject;

import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.commons.netty.Promises;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.security.oidc.config.WellKnownOpenIdConfig;
import com.simplyti.service.security.oidc.jwk.JsonWebKey;
import com.simplyti.service.security.oidc.jwk.JsonWebKeys;
import com.simplyti.service.security.oidc.key.WellKnownOpenIdJsonWebKeysProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class WellKnownOpenIdSigningKeyResolver implements SigningKeyResolver, ServerStartHook {
	
	private final HttpEndpoint endpoint;
	private final WellKnownOpenIdJsonWebKeysProvider provider;
	
	private JsonWebKeys keys;
	
	@Inject
	public WellKnownOpenIdSigningKeyResolver(WellKnownOpenIdConfig config,
			WellKnownOpenIdJsonWebKeysProvider provider) {
		this.provider=provider;
		this.endpoint=config.endpoint();
	}
	
	@Override
	public Future<Void> executeStart(EventLoop startStopLoop) {
		Promise<Void> promise = startStopLoop.newPromise();
		Future<JsonWebKeys> future = provider.get(endpoint,startStopLoop);
		Promises.ifSuccessContinue(future, promise, response->{
			this.keys=response;
			promise.setSuccess(null);
		});
		return promise;
	}

	@Override
	public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, Claims claims) {
		String keyId = (String) header.get("kid");
		Optional<JsonWebKey> optional = keys.keys().stream().filter(key->key.kid().equals(keyId)).findFirst();
		if(optional.isPresent()) {
			return optional.get().key();
		}else {
			throw new SigningKeyResolverException("Not found signing key in wellknown provider for key id "+keyId);
		}
	}

	@Override
	public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, String plaintext) {
		throw new SigningKeyResolverException(new UnsupportedOperationException());
	}

}
