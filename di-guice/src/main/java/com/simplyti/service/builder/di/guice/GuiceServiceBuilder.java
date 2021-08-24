package com.simplyti.service.builder.di.guice;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.simplyti.service.DefaultServer;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.fileserver.FileServeConfiguration;
import com.simplyti.service.ssl.SslConfig;

import io.netty.channel.EventLoopGroup;

public class GuiceServiceBuilder extends AbstractServiceBuilder implements ServiceBuilder {

	@Override
	protected DefaultServer build0(ServerConfig config, SslConfig sslConfig,FileServeConfiguration fileServerConfig, Stream<Module> additinalModules,
			Collection<Class<? extends ApiProvider>> apiClasses,
			EventLoopGroup eventLoopGroup) {
		ServiceModule coreModule = new ServiceModule(config,sslConfig,fileServerConfig,apiClasses,eventLoopGroup);
		Injector injector = Guice.createInjector(Modules.override(coreModule).with(additinalModules.collect(Collectors.toList())));
		return injector.getInstance(DefaultServer.class);
	}

}
