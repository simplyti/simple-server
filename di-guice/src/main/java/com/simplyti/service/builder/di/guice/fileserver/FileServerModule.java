package com.simplyti.service.builder.di.guice.fileserver;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.channel.handler.FileServeHandler;
import com.simplyti.service.channel.handler.inits.FileServerHandlerInit;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.fileserver.FileServe;
import com.simplyti.service.fileserver.FileServeConfiguration;

public class FileServerModule extends AbstractModule {

	private final FileServeConfiguration fileServerConfig;

	public FileServerModule(FileServeConfiguration fileServerConfig) {
		this.fileServerConfig=fileServerConfig;
	}

	@Override
	public void configure() {
		bind(FileServeConfiguration.class).toInstance(fileServerConfig);
		Multibinder.newSetBinder(binder(), HandlerInit.class).addBinding().to(FileServerHandlerInit.class).in(Singleton.class);
		bind(FileServeHandler.class).in(Singleton.class);
		bind(FileServe.class).in(Singleton.class);
	}

}
