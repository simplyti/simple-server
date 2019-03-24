package com.simplyti.service.builder.di;

import java.security.Provider;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import javax.inject.Singleton;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.simplyti.service.Service;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.filter.HttpRequetFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.api.health.HealthApi;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.DefaultServiceChannelInitializer;
import com.simplyti.service.channel.EntryChannelInit;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.channel.handler.FileServeHandler;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.sse.ServerSentEventEncoder;
import com.simplyti.service.ssl.DefaultServerCertificateProvider;
import com.simplyti.service.ssl.DefaultSslHandlerFactory;
import com.simplyti.service.ssl.IoCKeyManager;
import com.simplyti.service.ssl.IoCKeyManagerFactory;
import com.simplyti.service.ssl.IoCKeyManagerFactorySpi;
import com.simplyti.service.ssl.IoCSecurityProvider;
import com.simplyti.service.ssl.IoCTrustManager;
import com.simplyti.service.ssl.IoCTrustManagerFactory;
import com.simplyti.service.ssl.IoCTrustManagerFactorySpi;
import com.simplyti.service.ssl.ServerCertificateProvider;
import com.simplyti.service.ssl.SslHandlerFactory;
import com.simplyti.service.sync.DefaultSyncTaskSubmitter;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;

public class ServiceModule extends AbstractModule {
	
	private final ServerConfig config;
	private final Collection<Class<? extends ApiProvider>> apiClasses;
	private final EventLoopGroup eventLoopGroup;

	public ServiceModule(ServerConfig config, Collection<Class<? extends ApiProvider>> apiClasses, EventLoopGroup eventLoopGroup){
		this.config=config;
		this.apiClasses=apiClasses;
		this.eventLoopGroup=eventLoopGroup;
	}

	@Override
	protected void configure() {
		bind(new TypeLiteral<Service<?>>() {}).to(config.serviceClass()).in(Singleton.class);
		bind(ClientChannelGroup.class).in(Singleton.class);
		
		OptionalBinder.newOptionalBinder(binder(), EntryChannelInit.class);
		
		OptionalBinder.newOptionalBinder(binder(), DefaultBackendFullRequestHandler.class);
		OptionalBinder.newOptionalBinder(binder(), DefaultBackendRequestHandler.class);
		
		if(eventLoopGroup==null) {
			bind(EventLoopGroup.class).toProvider(EventLoopGroupProvider.class).in(Singleton.class);
		} else {
			bind(EventLoopGroup.class).toInstance(eventLoopGroup);
		}
		
		bind(EventLoop.class).annotatedWith(StartStopLoop.class).toProvider(StartStopLoopProvider.class).in(Singleton.class);
		bind(ServerConfig.class).toInstance(config);
		
		bind(ServiceChannelInitializer.class).to(DefaultServiceChannelInitializer.class).in(Singleton.class);
		
		OptionalBinder.newOptionalBinder(binder(), FileServeHandler.class);
		if(config.fileServe()!=null) {
			bind(FileServeHandler.class).in(Singleton.class);
		}
		
		bind(ServerSentEventEncoder.class).in(Singleton.class);
		bind(ExceptionHandler.class).in(Singleton.class);
		
		bind(ExecutorService.class).toProvider(ExecutorServiceProvider.class).in(Singleton.class);
		bind(SyncTaskSubmitter.class).to(DefaultSyncTaskSubmitter.class).in(Singleton.class);
	
		Multibinder<ApiProvider> mangerAPiProviders = Multibinder.newSetBinder(binder(), ApiProvider.class);
		Stream.concat(apiClasses.stream(), Stream.of(HealthApi.class))
			.forEach(apiClass->mangerAPiProviders.addBinding().to(apiClass).in(Singleton.class));
		
		Multibinder.newSetBinder(binder(), HttpRequetFilter.class);
		Multibinder.newSetBinder(binder(), OperationInboundFilter.class);
		Multibinder.newSetBinder(binder(), HttpResponseFilter.class);
		Multibinder.newSetBinder(binder(), ServerStartHook.class);
		Multibinder.newSetBinder(binder(), ServerStopHook.class);
		
		//SSL
		bind(Provider.class).to(IoCSecurityProvider.class).in(Singleton.class);
		bind(KeyManager.class).to(IoCKeyManager.class).in(Singleton.class);
		bind(KeyManagerFactorySpi.class).to(IoCKeyManagerFactorySpi.class).in(Singleton.class);
		bind(KeyManagerFactory.class).to(IoCKeyManagerFactory.class).in(Singleton.class);
		bind(TrustManager.class).to(IoCTrustManager.class).in(Singleton.class);
		bind(TrustManagerFactorySpi.class).to(IoCTrustManagerFactorySpi.class).in(Singleton.class);
		bind(TrustManagerFactory.class).to(IoCTrustManagerFactory.class).in(Singleton.class);
		
		bind(SslContext.class).toProvider(SslContextProvider.class).in(Singleton.class);
		bind(SslHandlerFactory.class).to(DefaultSslHandlerFactory.class).in(Singleton.class);
		
		OptionalBinder.newOptionalBinder(binder(), DefaultServerCertificateProvider.class);
		OptionalBinder.newOptionalBinder(binder(), ServerCertificateProvider.class);
		
	}

}
