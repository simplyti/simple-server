package com.simplyti.service.builder.di.dagger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.MoreObjects;
import com.simplyti.service.DefaultStartStopMonitor;
import com.simplyti.service.Listener;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.StartStopMonitor;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.builder.di.EventLoopGroupProvider;
import com.simplyti.service.builder.di.ExecutorServiceProvider;
import com.simplyti.service.builder.di.NativeIO;
import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.builder.di.StartStopLoopProvider;
import com.simplyti.service.builder.di.dagger.apibuilder.APIBuilderModule;
import com.simplyti.service.builder.di.dagger.defaultbackend.DefaultBackendModule;
import com.simplyti.service.builder.di.dagger.fileserver.FileServerModule;
import com.simplyti.service.builder.di.guice.DefaultHttpEntryChannelInit;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.DefaultServiceChannelInitializer;
import com.simplyti.service.channel.EntryChannelInit;
import com.simplyti.service.channel.ServerChannelFactoryProvider;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.channel.handler.ChannelExceptionHandler;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.json.DslJsonModule;
import com.simplyti.service.ssl.SslHandlerFactory;
import com.simplyti.service.sync.DefaultSyncTaskSubmitter;
import com.simplyti.service.sync.SyncTaskSubmitter;

import dagger.Module;
import dagger.Provides;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

@Module(includes= { Multibindings.class, APIBuilderModule.class, FileServerModule.class, DefaultBackendModule.class, DslJsonModule.class})
public class BaseServiceModule {
	
	private static final int DEFAULT_BLOCKING_THREAD_POOL = 500;
	private static final int DEFAULT_INSECURE_PORT = 8080;
	private static final int DEFAULT_SECURE_PORT = 8443;
	
	@Provides
	@Singleton
	public EventLoopGroup eventLoopGroup(Optional<NativeIO> nativeIO) {
		return new EventLoopGroupProvider(nativeIO).get();
	}
	
	@Provides @Singleton @StartStopLoop
	public EventLoop startStopLoop(ServerConfig config, Optional<NativeIO> nativeIO, ServerConfig serverConfig) {
		return new StartStopLoopProvider(nativeIO).get();
	}
	
	@Provides
	@Singleton
	public ServerConfig serverConfig(
			@Nullable @Named("name") String name,
			@Nullable @Named("blockingThreadPool") Integer blockingThreadPool,
			Set<Listener> listeners,
			@Nullable @Named("insecuredPort") Integer insecuredPort, 
			@Nullable @Named("securedPort") Integer  securedPort,
			@Nullable @Named("verbose") Boolean verbose) {
		return new ServerConfig(name,
				MoreObjects.firstNonNull(blockingThreadPool, DEFAULT_BLOCKING_THREAD_POOL),
				listeners(listeners,insecuredPort,securedPort),
				false, 
				MoreObjects.firstNonNull(verbose, false));
	}
	
	private Set<Listener> listeners(Set<Listener> listeners, Integer insecuredPort, Integer securedPort) {
		if(!listeners.isEmpty()) {
			return Collections.unmodifiableSet(listeners);
		}
		
		listeners = new HashSet<>();
		if(insecuredPort == null ) {
			listeners.add(new Listener(DEFAULT_INSECURE_PORT,false));
		} else if(insecuredPort>0) {
			listeners.add(new Listener(insecuredPort,false));
		}
		if(securedPort == null ) {
			listeners.add(new Listener(DEFAULT_SECURE_PORT,false));
		} else if(securedPort>0) {
			listeners.add(new Listener(securedPort,true));
		}
		return Collections.unmodifiableSet(listeners);
	}
	
	@Provides
	@Singleton
	public ChannelFactory<ServerChannel> channelFactory(Optional<NativeIO> nativeIO) {
		return new ServerChannelFactoryProvider(nativeIO).get();
	}
	
	@Provides
	@Singleton
	public EntryChannelInit entryChannelInit() {
		return new DefaultHttpEntryChannelInit();
	}

	@Provides
	@Singleton
	public StartStopMonitor startStopMonitor() {
		return new DefaultStartStopMonitor();
	}
	

	@Provides
	@Singleton
	public ClientChannelGroup clientChannelGroup(@StartStopLoop EventLoop startStopLoop) {
		return new ClientChannelGroup(startStopLoop);
	}

	@Provides
	@Singleton
	public ExceptionHandler exceptionHandler() {
		return new ExceptionHandler();
	}

	@Provides
	@Singleton
	public ChannelExceptionHandler apiExceptionHandler(ExceptionHandler exceptionHandler) {
		return new ChannelExceptionHandler(exceptionHandler);
	}
	
	@Provides
	@Singleton
	public ExecutorService executorService(ServerConfig serverConfig) {
		return new ExecutorServiceProvider(serverConfig).get();
	}

	@Provides
	@Singleton
	public SyncTaskSubmitter syncTaskSubmitter(ExecutorService executorService) {
		return new DefaultSyncTaskSubmitter(executorService);
	}

	@Provides
	@Singleton
	public ServerHeadersHandler serverHeadersHandler(ServerConfig serverConfig) {
		return new ServerHeadersHandler(serverConfig);
	}
	
	@Provides
	@Singleton
	public ServiceChannelInitializer serviceChannelInitializer(ClientChannelGroup clientChannelGroup,
			Optional<SslHandlerFactory> sslHandlerFactory, StartStopMonitor startStopMonitor,
			ChannelExceptionHandler channelExceptionHandler,
			Set<HandlerInit> handlers, Set<HttpRequestFilter> requestFilters, Set<HttpResponseFilter> responseFilters,
			EntryChannelInit entryChannelInit, ServerConfig serverConfig) {
		return new DefaultServiceChannelInitializer(clientChannelGroup, serverConfig, sslHandlerFactory.orElse(null),
				startStopMonitor, channelExceptionHandler, handlers, requestFilters, responseFilters,
				entryChannelInit);
	}
	
}