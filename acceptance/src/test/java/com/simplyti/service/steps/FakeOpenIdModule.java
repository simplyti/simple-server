package com.simplyti.service.steps;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.handler.ssl.util.SelfSignedCertificate;

public class FakeOpenIdModule extends AbstractModule {

	
	private final FakeOpenIdConfig config;

	public FakeOpenIdModule(SelfSignedCertificate key, String authEndpoint, String tokenEndpoint, int wellKnownDelay, int jwksDelay) {
		this.config = new FakeOpenIdConfig(key,tokenEndpoint,authEndpoint,wellKnownDelay,jwksDelay);
	}
	
	@Override
	public void configure() {
		bind(FakeOpenIdConfig.class).toInstance(config);
		Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(FakeOpenIdApi.class);
	}


}
