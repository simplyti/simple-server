package com.simplyti.service.security.oidc;

import javax.inject.Singleton;

import com.dslplatform.json.Configuration;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.security.oidc.config.WellKnownOpenIdConfig;
import com.simplyti.service.security.oidc.filter.OpenIdHeaderBasedRequestFilter;
import com.simplyti.service.security.oidc.jwk.JsonWebKeyConfiguration;
import com.simplyti.service.security.oidc.key.WellKnownOpenIdJsonWebKeysProvider;
import com.simplyti.service.security.oidc.key.resolver.WellKnownOpenIdSigningKeyResolver;

import io.jsonwebtoken.SigningKeyResolver;

public class WellKnownOpenIdModule extends AbstractModule{
	
	private final HttpEndpoint endpoint;

	public WellKnownOpenIdModule(String endpoint) {
		this.endpoint=HttpEndpoint.of(endpoint);
	}

	public void configure() {
		Multibinder.newSetBinder(binder(), Configuration.class).addBinding().to(JsonWebKeyConfiguration.class).in(Singleton.class);
		
		Multibinder.newSetBinder(binder(), HttpRequestFilter.class).addBinding().to(OpenIdHeaderBasedRequestFilter.class).in(Singleton.class);
		bind(WellKnownOpenIdConfig.class).toInstance(new WellKnownOpenIdConfig(endpoint));
		
		bind(WellKnownOpenIdJsonWebKeysProvider.class).in(Singleton.class);
		
		bind(WellKnownOpenIdSigningKeyResolver.class).in(Singleton.class);
		bind(SigningKeyResolver.class).to(WellKnownOpenIdSigningKeyResolver.class).in(Singleton.class);
		Multibinder.newSetBinder(binder(), ServerStartHook.class).addBinding().to(WellKnownOpenIdSigningKeyResolver.class).in(Singleton.class);
	}
}
