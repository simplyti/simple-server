package com.simplyti.service;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;

public class NotFoundFullHandlerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DefaultBackendFullRequestHandler.class).to(NotFoundFullHandler.class).in(Singleton.class);
	}

}
