package com.simplyti.service.builder.di.guice;

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
import com.simplyti.service.DefaultStartStopMonitor;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.StartStopMonitor;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.api.health.HealthApi;
import com.simplyti.service.builder.di.EventLoopGroupProvider;
import com.simplyti.service.builder.di.ExecutorServiceProvider;
import com.simplyti.service.builder.di.SslContextProvider;
import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.builder.di.StartStopLoopProvider;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.DefaultServiceChannelInitializer;
import com.simplyti.service.channel.EntryChannelInit;
import com.simplyti.service.channel.ServerChannelFactoryProvider;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.channel.handler.FileServeHandler;
import com.simplyti.service.channel.handler.inits.ApiRequestHandlerInit;
import com.simplyti.service.channel.handler.inits.DefaultBackendHandlerInit;
import com.simplyti.service.channel.handler.inits.FileServerHandlerInit;
import com.simplyti.service.channel.handler.inits.HandlerInit;
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

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.handler.ssl.SslContext;

public class ServiceModule extends AbstractModule {
	
	private final ServerConfig config;
	private final Collection<Class<? extends ApiProvider>> apiClasses;
	private final EventLoopGroup eventLoopGroup;
	private final Collection<ApiProvider> apiProviders;

	public ServiceModule(ServerConfig config, Collection<Class<? extends ApiProvider>> apiClasses, Collection<ApiProvider> apiProviders,  EventLoopGroup eventLoopGroup){
		this.config=config;
		this.apiClasses=apiClasses;
		this.apiProviders=apiProviders;
		this.eventLoopGroup=eventLoopGroup;
	}

	@Override
	protected void configure() {
		bind(ServerConfig.class).toInstance(config);
		
		bindEventLoop();
		bind(EventLoop.class).annotatedWith(StartStopLoop.class).toProvider(StartStopLoopProvider.class).in(Singleton.class);
		bind(StartStopMonitor.class).to(DefaultStartStopMonitor.class).in(Singleton.class);
		
		bind(new TypeLiteral<ChannelFactory<ServerChannel>>() {}).toProvider(ServerChannelFactoryProvider.class).in(Singleton.class);
		bind(ClientChannelGroup.class).in(Singleton.class);
		
		// Channel Initialized
		bind(ServiceChannelInitializer.class).to(DefaultServiceChannelInitializer.class).in(Singleton.class);
		OptionalBinder.newOptionalBinder(binder(), EntryChannelInit.class);
		
		
		// Default Handler
		Multibinder.newSetBinder(binder(), HandlerInit.class).addBinding().to(DefaultBackendHandlerInit.class).in(Singleton.class);
		OptionalBinder.newOptionalBinder(binder(), DefaultBackendFullRequestHandler.class);
		OptionalBinder.newOptionalBinder(binder(), DefaultBackendRequestHandler.class);
		
		// File server
		bindFileServer();
		
		// SSE Encoder
		bind(ServerSentEventEncoder.class).in(Singleton.class);
		
		// Exception Handler
		bind(ExceptionHandler.class).in(Singleton.class);
		
		// Sync operations
		bind(ExecutorService.class).toProvider(ExecutorServiceProvider.class).in(Singleton.class);
		bind(SyncTaskSubmitter.class).to(DefaultSyncTaskSubmitter.class).in(Singleton.class);
	
		// APIs
		Multibinder.newSetBinder(binder(), HandlerInit.class).addBinding().to(ApiRequestHandlerInit.class).in(Singleton.class);
		bindApis(Multibinder.newSetBinder(binder(), ApiProvider.class));
		
		// Filters
		Multibinder.newSetBinder(binder(), HttpRequestFilter.class);
		Multibinder.newSetBinder(binder(), OperationInboundFilter.class);
		Multibinder.newSetBinder(binder(), HttpResponseFilter.class);
		
		// Start-Stop Hooks
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

	private void bindFileServer() {
		if(config.fileServe()!=null) {
			Multibinder.newSetBinder(binder(), HandlerInit.class).addBinding().to(FileServerHandlerInit.class).in(Singleton.class);
			bind(FileServeHandler.class).in(Singleton.class);
		}
	}

	private void bindEventLoop() {
		if(eventLoopGroup==null) {
			bind(EventLoopGroup.class).toProvider(EventLoopGroupProvider.class).in(Singleton.class);
		} else {
			bind(EventLoopGroup.class).toInstance(eventLoopGroup);
		}
	}

	private void bindApis(Multibinder<ApiProvider> apiBinder) {
		apiProviders.forEach(provider->apiBinder.addBinding().toInstance(provider));
		Stream.concat(apiClasses.stream(), Stream.of(HealthApi.class)).forEach(apiClass->apiBinder.addBinding().to(apiClass).in(Singleton.class));
	}

}
