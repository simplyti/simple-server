package com.simplyti.service.builder.di.dagger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.simplyti.service.DefaultServer;
import com.simplyti.service.DefaultServerStopAdvisor;
import com.simplyti.service.Server;
import com.simplyti.service.ServerStopAdvisor;
import com.simplyti.service.builder.di.EventLoopGroupProvider;
import com.simplyti.service.builder.di.ExecutorServiceProvider;
import com.simplyti.service.builder.di.NativeIO;
import com.simplyti.service.builder.di.ServerTransportProvider;
import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.builder.di.StartStopLoopProvider;
import com.simplyti.service.builder.di.dagger.apibuilder.APIBuilderModule;
import com.simplyti.service.builder.di.dagger.defaultbackend.DefaultBackendOptionals;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.DefaultHttpEntryChannelInit;
import com.simplyti.service.channel.DefaultServiceChannelInitializer;
import com.simplyti.service.channel.EntryChannelInit;
import com.simplyti.service.channel.ServerDomainSocketChannelFactory;
import com.simplyti.service.channel.ServerSocketChannelFactory;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.channel.handler.ChannelExceptionHandler;
import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.channel.handler.DefaultHandler;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.channel.handler.inits.ServiceHadlerInit;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.exception.DefaultExceptionHandler;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.filter.http.FullHttpRequestFilter;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.service.filter.http.HttpResponseFilter;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.json.DslJsonModule;
import com.simplyti.service.ssl.SslHandlerFactory;
import com.simplyti.service.sync.DefaultSyncTaskSubmitter;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.service.transport.Listener;
import com.simplyti.service.transport.ServerTransport;
import com.simplyti.service.transport.tcp.TcpListener;

import dagger.Module;
import dagger.Provides;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.unix.ServerDomainSocketChannel;

@Module(includes= { Multibindings.class, APIBuilderModule.class, DslJsonModule.class, DefaultBackendOptionals.class})
public class BaseServiceModule {
	
	private static final int DEFAULT_BLOCKING_THREAD_POOL = 500;
	private static final Collection<Listener> DEFAULT_LISTENERS = Arrays.asList(new TcpListener(8080,false),new TcpListener(8443,true));
	private static final int DEFAULT_MAX_BODY_SIZE = 10000000;
	
	@Provides
	@Singleton
	public EventLoopGroup eventLoopGroup(Provider<Optional<NativeIO>> nativeIO) {
		return new EventLoopGroupProvider(nativeIO).get();
	}
	
	@Provides @Singleton @StartStopLoop
	public EventLoop startStopLoop(ServerConfig config, Provider<Optional<NativeIO>> nativeIO) {
		return new StartStopLoopProvider(nativeIO).get();
	}
	
	@Provides
	@Singleton
	public ServerConfig serverConfig(
			@Nullable @Named("name") String name,
			@Nullable @Named("blockingThreadPool") Integer blockingThreadPool, 
			@Nullable @Named("insecuredPort") Integer insecuredPort, 
			@Nullable @Named("securedPort") Integer  securedPort,
			@Nullable @Named("verbose") Boolean verbose,
			@Nullable @Named("maxBodySize") Integer maxBodySize) {
		return new ServerConfig(name,
				firstNonNull(blockingThreadPool, DEFAULT_BLOCKING_THREAD_POOL),
				DEFAULT_LISTENERS, 
				false, 
				firstNonNull(verbose, false),
				firstNonNull(maxBodySize, DEFAULT_MAX_BODY_SIZE));
	}
	
	private static <T> T firstNonNull(T o1, T o2) {
		if(o1 != null){
			return o1;
		}
		return o2;
	}

	@Provides
	@Singleton
	public ChannelFactory<ServerChannel> channelFactory(Provider<Optional<NativeIO>> nativeIO) {
		return new ServerSocketChannelFactory(nativeIO);
	}
	
	@Provides
	@Singleton
	public ChannelFactory<ServerDomainSocketChannel> channelDomainFactory(Provider<Optional<NativeIO>> nativeIO) {
		return new ServerDomainSocketChannelFactory(nativeIO);
	}
	
	@Provides
	@Singleton
	public EntryChannelInit entryChannelInit(ServerHeadersHandler serverHeadersHandler) {
		return new DefaultHttpEntryChannelInit(serverHeadersHandler);
	}

	@Provides
	@Singleton
	public ServerStopAdvisor startStopMonitor() {
		return new DefaultServerStopAdvisor();
	}
	

	@Provides
	@Singleton
	public ClientChannelGroup clientChannelGroup(@StartStopLoop Provider<EventLoop> startStopLoop) {
		return new ClientChannelGroup(startStopLoop.get());
	}

	@Provides
	@Singleton
	public ExceptionHandler exceptionHandler() {
		return new DefaultExceptionHandler();
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
	public ServiceChannelInitializer serviceChannelInitializer(Provider<ClientChannelGroup> clientChannelGroup,
			Optional<SslHandlerFactory> sslHandlerFactory, 
			ServerStopAdvisor startStopMonitor,
			ChannelExceptionHandler channelExceptionHandler,
			Set<HttpRequestFilter> requestFilters, Set<FullHttpRequestFilter> fullRequestFilters, Set<HttpResponseFilter> responseFilters,
			EntryChannelInit entryChannelInit, ServerConfig serverConfig, Set<ServiceHadlerInit> serviceHandlerInit,
			Provider<Optional<DefaultBackendFullRequestHandler>> defaultBackendFullRequestHandlerProvider,
			 Provider<Optional<DefaultBackendRequestHandler>> defaultBackendRequestHandlerProvider,
			 DefaultHandler defaultHandler) {
		return new DefaultServiceChannelInitializer(clientChannelGroup, serverConfig, sslHandlerFactory.orElse(null),
				startStopMonitor, channelExceptionHandler, requestFilters, fullRequestFilters, responseFilters,
				entryChannelInit, serviceHandlerInit,
				defaultBackendFullRequestHandlerProvider, defaultBackendRequestHandlerProvider,
				defaultHandler);
	}
	
	@Provides
	@Singleton
	public Set<ServerTransport> serverTransport(Provider<Optional<NativeIO>> nativeIO,Provider<EventLoopGroup> eventLoopGroup, @StartStopLoop Provider<EventLoop> startStopLoop,
			ChannelFactory<ServerChannel> channelFactory, ChannelFactory<ServerDomainSocketChannel> domainChannelFactory, Optional<SslHandlerFactory> sslHandlerFactory, 
			ServiceChannelInitializer serviceChannelInitializer, ServerConfig config) {
		return new ServerTransportProvider(nativeIO, eventLoopGroup, startStopLoop, channelFactory, domainChannelFactory, sslHandlerFactory, serviceChannelInitializer, config)
				.get();
	}
	
	@Provides
	@Singleton
	public Server server(Provider<EventLoopGroup> eventLoopGroup, @StartStopLoop Provider<EventLoop> startStopLoop,
			ServerStopAdvisor startStopMonitor,
			Provider<ClientChannelGroup> clientChannelGroup, ServerConfig config,
			Set<ServerStartHook> serverStartHook, Set<ServerStopHook> serverStopHook,
			Set<ServerTransport> transport) {
		return new DefaultServer(eventLoopGroup, startStopMonitor, startStopLoop, clientChannelGroup, serverStartHook, serverStopHook, config, transport, null);
	}
	
}