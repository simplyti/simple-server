package com.simplyti.service.builder.di.guice;

import com.google.inject.Module;
import com.simplyti.service.Service;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;

public interface ServiceBuilder<T extends Service<?>> {

	public T build();

	public ServiceBuilder<T> withLog4J2Logger();

	public ServiceBuilder<T> withApi(Class<? extends ApiProvider> apiClass);
	
	public ServiceBuilder<T> withApi(ApiProvider provider);
	
	public ServiceBuilder<T> withBlockingThreadPoolSize(int blockingThreadPoolSize);

	public ServiceBuilder<T> insecuredPort(int port);
	
	public ServiceBuilder<T> securedPort(int port);

	public ServiceBuilder<T> withModule(Class<? extends Module> module);
	
	public ServiceBuilder<T> withModule(Module module);

	public ServiceBuilder<T> fileServe(String path, String directory);

	public ServiceBuilder<T> withSlf4jLogger();

	public ServiceBuilder<T> disableInsecurePort();
	
	public ServiceBuilder<T> disableSecuredPort();

	public ServiceBuilder<T> eventLoopGroup(EventLoopGroup eventLoopGroup);

	public ServiceBuilder<T> verbose();

	public ServiceBuilder<T> withName(String name);
	
	public ServiceBuilder<T> withSslProvider(SslProvider sslProvider);

}
