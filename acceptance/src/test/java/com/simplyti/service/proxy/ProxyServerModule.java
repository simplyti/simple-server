package com.simplyti.service.proxy;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ProxyServerModule extends AbstractModule {

	@Override
	public void configure() {
		bind(ProxyServer.class).asEagerSingleton();
		Multibinder.newSetBinder(binder(), AutoCloseable.class).addBinding()
			.to(ProxyServer.class);
	}

}
