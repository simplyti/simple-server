package com.simplyti.service.builder.di.dagger;

import javax.annotation.Nullable;
import javax.inject.Named;

import dagger.BindsInstance;

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
	T insecuredPort(@Nullable @Named("insecuredPort") int insecuredPort);

	@BindsInstance
	T securedPort(@Nullable @Named("securedPort") int securedPort);

	@BindsInstance
	T verbose(@Nullable @Named("verbose") boolean verbose);

	O build();

}
