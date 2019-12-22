package com.simplyti.service.builder.di.dagger;


import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import io.netty.handler.ssl.SslProvider;

@Singleton
@Component(modules = { BaseServiceModule.class})
public interface ServiceBuilder {
	DaggerService build();
	
	@Component.Builder
	interface Builder {
		
		@BindsInstance Builder withName(@Nullable @Named("name") String name);
		@BindsInstance Builder withFileServerPath(@Nullable @Named("fileServerPath") String fileServerPath);
		@BindsInstance Builder withFileServerDirectory(@Nullable @Named("fileServerDirectory") String fileServerDirectory);
		@BindsInstance Builder withBlockingThreadPoolSize(@Nullable @Named("blockingThreadPool") int blockingThreadPool);
		@BindsInstance Builder withSslProvider(@Nullable @Named("sslProvider") SslProvider sslProvider);
		@BindsInstance Builder insecuredPort(@Nullable @Named("insecuredPort") int insecuredPort);
		@BindsInstance Builder securedPort(@Nullable @Named("securedPort") int securedPort);
		@BindsInstance Builder verbose(@Nullable @Named("verbose") boolean verbose);
		
		ServiceBuilder build();

	}
}
