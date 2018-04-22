package com.simplyti.service.builder.di;

import java.security.Provider;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import javax.inject.Singleton;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.simplyti.service.DefaultService;
import com.simplyti.service.Service;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.api.health.HealthApi;
import com.simplyti.service.api.shutdown.ShutdownApi;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.DefaultServiceChannelInitializer;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.channel.handler.FileServeHandler;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.ssl.IoCKeyManagerFactory;
import com.simplyti.service.ssl.IoCKeyManagerFactorySpi;
import com.simplyti.service.ssl.IoCSecurityProvider;
import com.simplyti.service.ssl.sni.DefaultServerCertificateProvider;
import com.simplyti.service.ssl.sni.SNIKeyManager;
import com.simplyti.service.ssl.sni.ServerCertificateProvider;
import com.simplyti.service.channel.handler.DefaultBackendHandler;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.handler.ssl.SslContext;

public class ServiceModule extends AbstractModule {
	
	private final ServerConfig config;
	private final Collection<Class<? extends ApiProvider>> apiClasses;

	public ServiceModule(ServerConfig config, Collection<Class<? extends ApiProvider>> apiClasses){
		this.config=config;
		this.apiClasses=apiClasses;
	}

	@Override
	protected void configure() {
		bind(Service.class).to(DefaultService.class).in(Singleton.class);
		bind(ClientChannelGroup.class).in(Singleton.class);
		
		OptionalBinder.newOptionalBinder(binder(), DefaultBackendHandler.class);
		
		bind(EventLoopGroup.class).toProvider(EventLoopGroupProvider.class).in(Singleton.class);
		bind(new TypeLiteral<Class<? extends ServerSocketChannel>>() {}).toProvider(SererChannelClassProvider.class).in(Singleton.class);
		bind(EventLoop.class).annotatedWith(StartStopLoop.class).toProvider(StartStopLoopProvider.class).in(Singleton.class);
		bind(ServerConfig.class).toInstance(config);
		
		bind(ServiceChannelInitializer.class).to(DefaultServiceChannelInitializer.class).in(Singleton.class);
		bind(FileServeHandler.class).in(Singleton.class);
		
		bind(ExecutorService.class).toProvider(ExecutorServiceProvider.class).in(Singleton.class);
	
		Multibinder<ApiProvider> mangerAPiProviders = Multibinder.newSetBinder(binder(), ApiProvider.class);
		Stream.concat(apiClasses.stream(), Stream.of(HealthApi.class,ShutdownApi.class))
			.forEach(apiClass->mangerAPiProviders.addBinding().to(apiClass).in(Singleton.class));
		
		Multibinder.newSetBinder(binder(), OperationInboundFilter.class);
		Multibinder.newSetBinder(binder(), ServerStartHook.class);
		Multibinder.newSetBinder(binder(), ServerStopHook.class);
		
		//SSL
		bind(Provider.class).to(IoCSecurityProvider.class).in(Singleton.class);
		bind(KeyManager.class).to(SNIKeyManager.class).in(Singleton.class);
		bind(KeyManagerFactorySpi.class).to(IoCKeyManagerFactorySpi.class).in(Singleton.class);
		bind(KeyManagerFactory.class).to(IoCKeyManagerFactory.class).in(Singleton.class);
		bind(SslContext.class).toProvider(SslContextProvider.class).in(Singleton.class);
		
		OptionalBinder.newOptionalBinder(binder(), DefaultServerCertificateProvider.class);
		OptionalBinder.newOptionalBinder(binder(), ServerCertificateProvider.class);
		
	}

}
