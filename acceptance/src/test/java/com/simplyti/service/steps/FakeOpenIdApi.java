package com.simplyti.service.steps;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.serializer.json.Json;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vavr.control.Try;

public class FakeOpenIdApi implements ApiProvider {
	
	@Inject
	private FakeOpenIdConfig config;
	
	@Inject
	private Json json;

	@Override
	public void build(ApiBuilder builder) {
		builder.when().post(config.tokenEndpoint())
		.then(ctx->{
			String token = Jwts.builder()
			  .setSubject("Joe")
			  .signWith(config.key().key(),SignatureAlgorithm.RS256)
			  .setHeaderParam("kid", "thekey")
			  .serializeToJsonWith(json::serialize)
			  .compact();
			IdentityToken id = new IdentityToken("XXXXXX", "Bearer", token, "ZZZZZZ", 1000);
			ctx.send(id);
		});
		
		builder.when().get("/.well-known/openid-configuration")
		.then(ctx->{
			ImmutableMap<String, Object> data = ImmutableMap.<String, Object>builder()
					.put("authorization_endpoint", "https://localhost:7443"+config.authEndpoint())
					.put("token_endpoint", "https://localhost:7443"+config.tokenEndpoint())
					.put("jwks_uri","https://localhost:7443/.well-known/jwks.json").build();
			if(config.wellKnownDelay()>0) {
				ctx.executor().schedule(()->ctx.send(data), config.wellKnownDelay(), TimeUnit.MILLISECONDS);
			}else {
				ctx.send(data);
			}
		});
		
		builder.when().get("/.well-known/jwks.json")
		.then(ctx->{
			ImmutableMap<String, Object> data = ImmutableMap.<String, Object>builder()
					.put("keys", ImmutableSet.<Map<String,Object>>builder()
							.add(ImmutableMap.<String, Object>builder()
									.put("alg","RS256")
									.put("kid","thekey")
									.put("x5c",ImmutableSet.<String>builder()
											.add(Base64.getEncoder().encodeToString(Try.<byte[]>of(()->config.key().cert().getEncoded()).get()))
											.build())
									.build())
							.build()).build();
			if(config.jwksDelay()>0) {
				ctx.executor().schedule(()->ctx.send(data), config.jwksDelay(), TimeUnit.MILLISECONDS);
			}else {
				ctx.send(data);
			}
		});
	}
	
}
