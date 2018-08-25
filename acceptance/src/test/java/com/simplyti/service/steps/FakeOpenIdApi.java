package com.simplyti.service.steps;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.vavr.control.Try;

public class FakeOpenIdApi extends AbstractModule implements ApiProvider {

	private final SelfSignedCertificate key;
	private final String tokenEndpoint;
	private final String authEndpoint;
	private final int wellKnownDelay;
	private final int jwksDelay;

	public FakeOpenIdApi(SelfSignedCertificate key, String authEndpoint, String tokenEndpoint, int wellKnownDelay, int jwksDelay) {
		this.key=key;
		this.authEndpoint=authEndpoint;
		this.tokenEndpoint=tokenEndpoint;
		this.wellKnownDelay=wellKnownDelay;
		this.jwksDelay=jwksDelay;
	}
	
	@Override
	public void configure() {
		Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().toInstance(this);
	}

	@Override
	public void build(ApiBuilder builder) {
		builder.when().post(tokenEndpoint)
		.then(ctx->{
			String token = Jwts.builder()
			  .setSubject("Joe")
			  .signWith(SignatureAlgorithm.RS256, key.key())
			  .setHeaderParam("kid", "thekey")
			  .compact();
			IdentityToken id = new IdentityToken("XXXXXX", "Bearer", token, "ZZZZZZ", 1000);
			ctx.send(id);
		});
		
		builder.when().get("/.well-known/openid-configuration")
		.then(ctx->{
			ImmutableMap<String, Object> data = ImmutableMap.<String, Object>builder()
					.put("authorization_endpoint", "https://localhost:7443"+authEndpoint)
					.put("token_endpoint", "https://localhost:7443"+tokenEndpoint)
					.put("jwks_uri","https://localhost:7443/.well-known/jwks.json").build();
			if(wellKnownDelay>0) {
				ctx.executor().schedule(()->ctx.send(data), wellKnownDelay, TimeUnit.MILLISECONDS);
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
											.add(Base64.getEncoder().encodeToString(Try.<byte[]>of(()->key.cert().getEncoded()).get()))
											.build())
									.build())
							.build()).build();
			if(jwksDelay>0) {
				ctx.executor().schedule(()->ctx.send(data), jwksDelay, TimeUnit.MILLISECONDS);
			}else {
				ctx.send(data);
			}
		});
	}


}
