package com.simplyti.service.builder.di.guice.fileserver;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.channel.handler.inits.ServiceHadlerInit;
import com.simplyti.service.fileserver.FileServe;
import com.simplyti.service.fileserver.FileServeConfiguration;
import com.simplyti.service.fileserver.handler.FileServeHandler;
import com.simplyti.service.fileserver.handler.init.FileServerHandlerInit;

public class FileServerModule extends AbstractModule {

	private final FileServeConfiguration fileServerConfig;

	public FileServerModule(FileServeConfiguration fileServerConfig) {
		this.fileServerConfig=fileServerConfig;
	}

	@Override
	public void configure() {
		if(fileServerConfig!=null) {
			bind(FileServeConfiguration.class).toInstance(fileServerConfig);
			Multibinder.newSetBinder(binder(), ServiceHadlerInit.class).addBinding().to(FileServerHandlerInit.class).in(Singleton.class);
			bind(FileServeHandler.class).in(Singleton.class);
			bind(FileServe.class).in(Singleton.class);
		}
	}

}
