package com.simplyti.service.builder.di.guice;

import com.google.inject.Module;
import com.simplyti.service.DefaultServer;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;

public interface ServiceBuilder {

	public DefaultServer build();

	public ServiceBuilder withLog4J2Logger();

	public ServiceBuilder withApi(Class<? extends ApiProvider> apiClass);
	
	public ServiceBuilder withBlockingThreadPoolSize(int blockingThreadPoolSize);

	public ServiceBuilder insecuredPort(int port);
	
	public ServiceBuilder securedPort(int port);

	public ServiceBuilder withModule(Class<? extends Module> module);
	
	public ServiceBuilder withModule(Module module);

	public ServiceBuilder fileServe(String path, String directory);

	public ServiceBuilder withSlf4jLogger();

	public ServiceBuilder disableInsecurePort();
	
	public ServiceBuilder disableSecuredPort();

	public ServiceBuilder eventLoopGroup(EventLoopGroup eventLoopGroup);

	public ServiceBuilder verbose();
	
	public ServiceBuilder withMaxBodySize(int maxBodySize);

	public ServiceBuilder withName(String name);
	
	public ServiceBuilder withSslProvider(SslProvider sslProvider);

}
