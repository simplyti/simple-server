package com.simplyti.service.builder.di.dagger;

import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Named;

import com.simplyti.service.transport.Listener;

import dagger.BindsInstance;
import io.netty.handler.ssl.SslProvider;

public interface AbstractServiceBuilder<T extends AbstractServiceBuilder<T,O>,O extends AbstractService> {
	
	@BindsInstance
	T withFileServerPath(@Nullable @Named("fileServerPath") String fileServerPath);
	
	@BindsInstance
	T withFileServerDirectory(@Nullable @Named("fileServerDirectory") String fileServerDirectory);
	
	@BindsInstance
	T withName(@Nullable @Named("name") String name);
	
	@BindsInstance
	T withBlockingThreadPoolSize(@Nullable @Named("blockingThreadPool") int blockingThreadPool);
	
	@BindsInstance
	T withSslProvider(@Nullable @Named("sslProvider") SslProvider sslProvider);
	
	@BindsInstance
	T verbose(@Nullable @Named("verbose") boolean verbose);
	
	@BindsInstance
	T withMaxBodySize(@Nullable @Named("maxBodySize") int maxBodySize);
	
	@BindsInstance
	T withListener(@Nullable @Named("listeners") Set<Listener> listener);
	
	O build();

}
