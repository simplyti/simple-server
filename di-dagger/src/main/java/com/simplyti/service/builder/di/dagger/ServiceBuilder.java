package com.simplyti.service.builder.di.dagger;


import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import com.simplyti.service.transport.Listener;

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
		@BindsInstance Builder verbose(@Nullable @Named("verbose") boolean verbose);
		@BindsInstance Builder maxBodySize(@Nullable @Named("maxBodySize") int maxBodySize);
		@BindsInstance Builder withListener(@Nullable @Named("listeners") Set<Listener> listener);
		
		ServiceBuilder build();

	}
}
