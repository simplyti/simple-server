package com.simplyti.service.builder.di.guice;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.simplyti.service.DefaultService;
import com.simplyti.service.DefaultStartStopMonitor;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.Service;
import com.simplyti.service.StartStopMonitor;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.builder.di.EventLoopGroupProvider;
import com.simplyti.service.builder.di.ExecutorServiceProvider;
import com.simplyti.service.builder.di.NativeIO;
import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.builder.di.StartStopLoopProvider;
import com.simplyti.service.builder.di.guice.apibuilder.APIBuilderModule;
import com.simplyti.service.builder.di.guice.defaultbackend.DefaultBackendModule;
import com.simplyti.service.builder.di.guice.fileserver.FileServerModule;
import com.simplyti.service.builder.di.guice.nativeio.NativeIOModule;
import com.simplyti.service.builder.di.guice.ssl.SSLModule;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.DefaultHttpEntryChannelInit;
import com.simplyti.service.channel.DefaultServiceChannelInitializer;
import com.simplyti.service.channel.EntryChannelInit;
import com.simplyti.service.channel.ServerChannelFactoryProvider;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.fileserver.FileServeConfiguration;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.json.DslJsonModule;
import com.simplyti.service.ssl.SslConfig;
import com.simplyti.service.ssl.SslHandlerFactory;
import com.simplyti.service.sync.DefaultSyncTaskSubmitter;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public class ServiceModule extends AbstractModule {
	
	private final ServerConfig config;
	private final SslConfig sslConfig;
	private final FileServeConfiguration fileServerConfig;
	private final Collection<Class<? extends ApiProvider>> apiClasses;
	private final EventLoopGroup eventLoopGroup;
	private final Collection<ApiProvider> apiProviders;

	public ServiceModule(ServerConfig config, SslConfig sslConfig, FileServeConfiguration fileServerConfig, 
			Collection<Class<? extends ApiProvider>> apiClasses, Collection<ApiProvider> apiProviders,  
			EventLoopGroup eventLoopGroup){
		this.sslConfig=sslConfig;
		this.config=config;
		this.fileServerConfig=fileServerConfig;
		this.apiClasses=apiClasses;
		this.apiProviders=apiProviders;
		this.eventLoopGroup=eventLoopGroup;
	}

	@Override
	protected void configure() {
		install(new DslJsonModule());
		install(new SSLModule(sslConfig));
		install(new NativeIOModule());
		install(new APIBuilderModule(apiProviders,apiClasses));
		install(new DefaultBackendModule());
		installFileServer();
		bind(ServerConfig.class).toInstance(config);
		
		bindEventLoop();
		OptionalBinder.newOptionalBinder(binder(), NativeIO.class);
		bind(EventLoop.class).annotatedWith(StartStopLoop.class).toProvider(StartStopLoopProvider.class).in(Singleton.class);
		bind(StartStopMonitor.class).to(DefaultStartStopMonitor.class).in(Singleton.class);
		
		bind(new TypeLiteral<ChannelFactory<ServerChannel>>() {}).toProvider(ServerChannelFactoryProvider.class).in(Singleton.class);
		bind(ClientChannelGroup.class).in(Singleton.class);
		bind(EntryChannelInit.class).to(DefaultHttpEntryChannelInit.class).in(Singleton.class);
		
		// Channel Initialized
		bind(ServiceChannelInitializer.class).to(DefaultServiceChannelInitializer.class).in(Singleton.class);
		bind(ServerHeadersHandler.class).in(Singleton.class);
		
		bind(new TypeLiteral<Service<?>>() {}).to(DefaultService.class).in(Singleton.class);
		
		// Exception Handler
		bind(ExceptionHandler.class).in(Singleton.class);
		
		// Sync operations
		bind(ExecutorService.class).toProvider(ExecutorServiceProvider.class).in(Singleton.class);
		bind(SyncTaskSubmitter.class).to(DefaultSyncTaskSubmitter.class).in(Singleton.class);
	
		// Filters
		Multibinder.newSetBinder(binder(), HttpRequestFilter.class);
		Multibinder.newSetBinder(binder(), OperationInboundFilter.class);
		Multibinder.newSetBinder(binder(), HttpResponseFilter.class);
		
		// Start-Stop Hooks
		Multibinder.newSetBinder(binder(), ServerStartHook.class);
		Multibinder.newSetBinder(binder(), ServerStopHook.class);
		
		OptionalBinder.newOptionalBinder(binder(), NativeIO.class);
		OptionalBinder.newOptionalBinder(binder(), SslHandlerFactory.class);
	}

	private void installFileServer() {
		if(fileServerConfig!=null) {
			install(new FileServerModule(fileServerConfig));
		}
	}

	private void bindEventLoop() {
		if(eventLoopGroup==null) {
			bind(EventLoopGroup.class).toProvider(EventLoopGroupProvider.class).in(Singleton.class);
		} else {
			bind(EventLoopGroup.class).toInstance(eventLoopGroup);
		}
	}

}
