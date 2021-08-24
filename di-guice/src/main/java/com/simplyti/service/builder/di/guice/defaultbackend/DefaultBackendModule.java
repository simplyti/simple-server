package com.simplyti.service.builder.di.guice.defaultbackend;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.OptionalBinder;
import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;

public class DefaultBackendModule extends AbstractModule {

	@Override
	public void configure() {
		OptionalBinder.newOptionalBinder(binder(), DefaultBackendFullRequestHandler.class);
		OptionalBinder.newOptionalBinder(binder(), DefaultBackendRequestHandler.class);
	}

}
