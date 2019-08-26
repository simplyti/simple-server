package com.simplyti.service.builder.di.dagger;

import java.util.Optional;

import javax.inject.Provider;

import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.channel.handler.inits.DefaultBackendHandlerInit;
import com.simplyti.service.channel.handler.inits.HandlerInit;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module(includes= {DefaultBackendOptionals.class})
public class DefaultBackend {
	
	@Provides
	@IntoSet
	public HandlerInit defaultBackendHandlerInit(
			Optional<DefaultBackendFullRequestHandler> defaultBackendFullRequestHandler,
			Provider<Optional<DefaultBackendRequestHandler>> defaultBackendRequestHandler) {
		return new DefaultBackendHandlerInit(defaultBackendFullRequestHandler, defaultBackendRequestHandler);
	}
	
}
