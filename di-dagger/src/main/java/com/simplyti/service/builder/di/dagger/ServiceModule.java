package com.simplyti.service.builder.di.dagger;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.MoreObjects;
import com.simplyti.service.DefaultStartStopMonitor;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.StartStopMonitor;
import com.simplyti.service.api.builder.di.InstanceProvider;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.builder.di.EventLoopGroupProvider;
import com.simplyti.service.builder.di.ExecutorServiceProvider;
import com.simplyti.service.builder.di.StartStopLoop;
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
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

@Module(includes= { Multibindings.class, ApiServer.class, FileServerModule.class, DefaultBackend.class, SslModule.class, EntryInit.class, DslJsonModule.class})
public class ServiceModule {
	
	@Provides
	@Singleton
	public EventLoopGroup eventLoopGroup() {
		return new EventLoopGroupProvider().get();
	}
	
	@Provides @Singleton @StartStopLoop
	public EventLoop startStopLoop(ServerConfig config, EventLoopGroup eventLoopGroup, ServerConfig serverConfig) {
		if(Epoll.isAvailable()) {
			return new EpollEventLoopGroup(1).next();
		}else if(KQueue.isAvailable()) {
			return new KQueueEventLoopGroup(1).next();
		}else {
			return new NioEventLoopGroup(1).next();
		}
	}
	
	@Provides
	@Singleton
	public ServerConfig serverConfig(
			@Nullable @Named("name") String name,
			@Nullable @Named("blockingThreadPool") Integer blockingThreadPool, 
			@Nullable @Named("insecuredPort") Integer insecuredPort, 
			@Nullable @Named("securedPort") Integer  securedPort,
			@Nullable @Named("verbose") Boolean verbose) {
		return new ServerConfig(name,
				MoreObjects.firstNonNull(blockingThreadPool, 500),
				MoreObjects.firstNonNull(insecuredPort, 8080),
				MoreObjects.firstNonNull(securedPort, 8443), 
				false, 
				MoreObjects.firstNonNull(verbose, false));
	}
	
	@Provides
	@Singleton
	public ChannelFactory<ServerChannel> channelFactory(EventLoopGroup eventLoopGroup) {
		return new ServerChannelFactoryProvider(eventLoopGroup).get();
	}

	@Provides
	@Singleton
	public StartStopMonitor startStopMonitor() {
		return new DefaultStartStopMonitor();
	}
	
	@Provides
	@Singleton
	public InstanceProvider instanceProvider(Map<Class<?>,Object> instances) {
		return new DaggerInstanceProvider(instances);
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
			Optional<EntryChannelInit> entryChannelInit, ServerConfig serverConfig) {
		return new DefaultServiceChannelInitializer(clientChannelGroup, serverConfig, sslHandlerFactory.orElse(null),
				startStopMonitor, channelExceptionHandler, handlers, requestFilters, responseFilters,
				entryChannelInit);
	}
	
}