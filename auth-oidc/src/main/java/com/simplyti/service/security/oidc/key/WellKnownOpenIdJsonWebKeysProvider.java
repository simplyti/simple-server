package com.simplyti.service.security.oidc.key;

import javax.inject.Inject;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.security.oidc.config.auto.WellKnownOpenIdResponse;
import com.simplyti.service.security.oidc.jwk.JsonWebKeys;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class WellKnownOpenIdJsonWebKeysProvider {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(WellKnownOpenIdJsonWebKeysProvider.class);
	
	private final HttpClient http;
	private final Json json;
	
	@Inject
	public WellKnownOpenIdJsonWebKeysProvider(Json json, EventLoopGroup eventLoopGroup) {
		this.http = HttpClient.builder()
				.withEventLoopGroup(eventLoopGroup)
				.build();
		this.json=json;
	}

	public Future<JsonWebKeys> get(HttpEndpoint endpoint, EventLoop startStopLoop) {
		Promise<JsonWebKeys> promise = startStopLoop.newPromise();
		log.info("Resolving well-known openid configuration from {}",endpoint);
		Future<WellKnownOpenIdResponse> futureResponse = http.request()
				.withEndpoint(endpoint)
				.withCheckStatusCode()
				.get("/.well-known/openid-configuration")
				.fullResponse(response->json.deserialize(response.content(), WellKnownOpenIdResponse.class));
			futureResponse.addListener(f->{
				if(f.isSuccess()) {
					WellKnownOpenIdResponse oidc = futureResponse.getNow();
					getJwsKey(oidc.jwks_uri(),promise);
				} else {
					promise.setFailure(f.cause());
				}
			});
		return promise;
	}
	
	private void getJwsKey(String jwksUri, Promise<JsonWebKeys> promise) {
		HttpEndpoint endpoint = HttpEndpoint.of(jwksUri);
		Future<JsonWebKeys> futureResponse = http.request()
			.withEndpoint(endpoint )
			.withCheckStatusCode()
			.get(endpoint.path())
			.fullResponse(response->json.deserialize(response.content(), JsonWebKeys.class));
		futureResponse.addListener(f->{
			if(f.isSuccess()) {
				promise.setSuccess(futureResponse.getNow());
			} else {
				promise.setFailure(f.cause());
			}
		});
	}

}
