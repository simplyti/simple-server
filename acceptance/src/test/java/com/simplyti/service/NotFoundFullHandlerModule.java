package com.simplyti.service;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.examples.api.APITest;

public class NotFoundFullHandlerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DefaultBackendFullRequestHandler.class).to(NotFoundFullHandler.class).in(Singleton.class);
		Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(APITest.class);
	}

}
