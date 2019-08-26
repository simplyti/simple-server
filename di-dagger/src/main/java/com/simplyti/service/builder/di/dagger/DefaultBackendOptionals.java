package com.simplyti.service.builder.di.dagger;

import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;

import dagger.BindsOptionalOf;
import dagger.Module;

@Module
public interface DefaultBackendOptionals {

	@BindsOptionalOf DefaultBackendFullRequestHandler defaultBackendFullRequestHandler();
	@BindsOptionalOf DefaultBackendRequestHandler defaultBackendRequestHandler();
	
}
