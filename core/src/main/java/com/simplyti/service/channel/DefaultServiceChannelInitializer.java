package com.simplyti.service.channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.ServerStopAdvisor;
import com.simplyti.service.channel.handler.ChannelExceptionHandler;
import com.simplyti.service.channel.handler.ClientChannelHandler;
import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.channel.handler.DefaultHandler;
import com.simplyti.service.channel.handler.FullHttpRequestFilterHandler;
import com.simplyti.service.channel.handler.HttpRequestFilterHandler;
import com.simplyti.service.channel.handler.HttpResponseFilterHandler;
import com.simplyti.service.channel.handler.inits.ServiceHadlerInit;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.filter.http.FullHttpRequestFilter;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.service.filter.http.HttpResponseFilter;
import com.simplyti.service.priority.Priorized;
import com.simplyti.service.ssl.SslHandlerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Sharable
public class DefaultServiceChannelInitializer extends ChannelInitializer<Channel> implements ServiceChannelInitializer  {

	private final ClientChannelGroup clientChannelGroup;
	private final ServerConfig serverConfig;
	private final SslHandlerFactory sslHandlerFactory;
	private final ServerStopAdvisor startStopMonitor;
	private final ChannelExceptionHandler channelExceptionHandler;
	private final List<HttpRequestFilter> requestFilters;
	private final List<FullHttpRequestFilter> fullRequestFilters;
	private final List<HttpResponseFilter> responseFilters;
	private final EntryChannelInit entryChannelInit;
	private final List<ServiceHadlerInit> serviceHandlerInit;
	private final Provider<Optional<DefaultBackendFullRequestHandler>> defaultBackendFullRequestHandlerProvider;
	private final Provider<Optional<DefaultBackendRequestHandler>> defaultBackendRequestHandlerProvider;
	private final DefaultHandler defaultHandler;
	private final FullHttpRequestFilterHandler fullHttpRequestFilterHandler;
	
	@Inject
	public DefaultServiceChannelInitializer(ClientChannelGroup clientChannelGroup, ServerConfig serverConfig,
			SslHandlerFactory sslHandlerFactory, ServerStopAdvisor startStopMonitor, ChannelExceptionHandler channelExceptionHandler,
			Set<HttpRequestFilter> requestFilters, Set<FullHttpRequestFilter> fullRequestFilters, Set<HttpResponseFilter> responseFilters,
			 EntryChannelInit entryChannelInit, Set<ServiceHadlerInit> serviceHandlerInit,
			 Provider<Optional<DefaultBackendFullRequestHandler>> defaultBackendFullRequestHandlerProvider,
			 Provider<Optional<DefaultBackendRequestHandler>> defaultBackendRequestHandlerProvider,
			 DefaultHandler defaultHandler) {
		this.clientChannelGroup=clientChannelGroup;
		this.serverConfig=serverConfig;
		this.sslHandlerFactory=sslHandlerFactory;
		this.startStopMonitor=startStopMonitor;
		this.channelExceptionHandler=channelExceptionHandler;
		this.requestFilters=requestFilters.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
		this.fullRequestFilters=fullRequestFilters.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
		this.responseFilters=responseFilters.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
		this.entryChannelInit=entryChannelInit;
		this.serviceHandlerInit=serviceHandlerInit.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
		this.defaultBackendFullRequestHandlerProvider=defaultBackendFullRequestHandlerProvider;
		this.defaultBackendRequestHandlerProvider=defaultBackendRequestHandlerProvider;
		this.defaultHandler=defaultHandler;
		this.fullHttpRequestFilterHandler= new FullHttpRequestFilterHandler(this.fullRequestFilters);
	}
	
	@Override
	protected void initChannel(Channel channel) throws Exception {
		clientChannelGroup.add(channel);
		ChannelPipeline pipeline = channel.pipeline();
		
		if(isSslChannel(channel)) {
			pipeline.addLast("ssl",sslHandlerFactory.handler(channel));
		}
		
		if(serverConfig.verbose()) {
			pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		}
		
		entryChannelInit.init(pipeline);	
		
		pipeline.addLast(new ClientChannelHandler(startStopMonitor));
		if(!responseFilters.isEmpty()) {
			pipeline.addLast(new HttpResponseFilterHandler(responseFilters));
		}
		if(!requestFilters.isEmpty()) {
			pipeline.addLast(new HttpRequestFilterHandler(requestFilters));
		}
		
		Optional<DefaultBackendRequestHandler> defaultBackendRequestHandler = defaultBackendRequestHandlerProvider.get();
		Optional<DefaultBackendFullRequestHandler> defaultBackendFullRequestHandler = this.defaultBackendFullRequestHandlerProvider.get();
		
		
		if(defaultBackendFullRequestHandler.isPresent() || !fullRequestFilters.isEmpty()) {
			pipeline.addLast(new HttpObjectAggregator(serverConfig.maxBodySize()));
		}
		
		if(!fullRequestFilters.isEmpty()) {
			pipeline.addLast(fullHttpRequestFilterHandler);
		}
		
		pipeline.addLast("default-handler",defaultHandler);
		
		if(defaultBackendRequestHandler.isPresent()) {
			pipeline.addLast(defaultBackendRequestHandler.get());
		}
		
		serviceHandlerInit.forEach(init->{
			init.init(pipeline);
		});
		
		if(defaultBackendFullRequestHandler.isPresent()) {
			pipeline.addLast(defaultBackendFullRequestHandler.get());
		}
		
		pipeline.addLast(channelExceptionHandler);
	}

	private boolean isSslChannel(Channel channel) {
		return channel instanceof SocketChannel &&  ((InetSocketAddress)channel.localAddress()).getPort()==serverConfig.securedPort();
	}

}
