package com.simplyti.service;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;

public class NotFoundHandlerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DefaultBackendRequestHandler.class).to(NotFoundHandler.class).in(Singleton.class);
	}

}
