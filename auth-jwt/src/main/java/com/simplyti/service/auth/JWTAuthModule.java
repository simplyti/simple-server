package com.simplyti.service.auth;

import java.security.Key;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.filter.OperationInboundFilter;


public class JWTAuthModule  extends AbstractModule {

	private JWTConfiguration jwtConfiguration;

	public JWTAuthModule(Key key) {
		this.jwtConfiguration = new DefaultJWTConfiguration(key);
	}

	@Override
	protected void configure() {
		bind(JWTConfiguration.class).toInstance(jwtConfiguration);
		
		Multibinder<OperationInboundFilter> fulters = Multibinder.newSetBinder(binder(), OperationInboundFilter.class);
		fulters.addBinding().to(JWTAuthFilter.class).in(Singleton.class);
	}


}