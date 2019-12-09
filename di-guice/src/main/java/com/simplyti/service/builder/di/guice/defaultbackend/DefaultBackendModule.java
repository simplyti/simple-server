package com.simplyti.service.builder.di.guice.defaultbackend;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.channel.handler.inits.DefaultBackendHandlerInit;
import com.simplyti.service.channel.handler.inits.HandlerInit;

public class DefaultBackendModule extends AbstractModule {

	@Override
	public void configure() {
		Multibinder.newSetBinder(binder(), HandlerInit.class).addBinding().to(DefaultBackendHandlerInit.class).in(Singleton.class);
		
		OptionalBinder.newOptionalBinder(binder(), DefaultBackendFullRequestHandler.class);
		OptionalBinder.newOptionalBinder(binder(), DefaultBackendRequestHandler.class);

	}

}
