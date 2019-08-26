package com.simplyti.service.builder.di.guice;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.Service;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.channel.EventLoopGroup;

public class GuiceServiceBuilder<T extends Service<?>> extends AbstractServiceBuilder<T> implements ServiceBuilder<T> {

	public GuiceServiceBuilder(Class<T> serviceClass) {
		super(serviceClass);
	}

	@Override
	protected T build0(ServerConfig config, Class<T> serviceClass, Stream<Module> additinalModules,
			Collection<Class<? extends ApiProvider>> apiClasses, Collection<ApiProvider> apiProviders,
			EventLoopGroup eventLoopGroup) {
		ServiceModule coreModule = new ServiceModule(config,apiClasses,apiProviders,eventLoopGroup);
		Injector injector = Guice.createInjector(Modules.override(coreModule).with(additinalModules.collect(Collectors.toList())));
		return injector.getInstance(serviceClass);
	}
	
}
