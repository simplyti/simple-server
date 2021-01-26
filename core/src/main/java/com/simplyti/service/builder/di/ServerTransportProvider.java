package com.simplyti.service.builder.di;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.ssl.SslHandlerFactory;
import com.simplyti.service.transport.ServerTransport;
import com.simplyti.service.transport.tcp.NioServerTransport;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public class ServerTransportProvider implements Provider<ServerTransport>{
	
	private final Optional<NativeIO> nativeIO;
	private final EventLoopGroup eventLoopGroup;
	private final EventLoop startStopLoop;
	private final ChannelFactory<ServerChannel> channelFactory;
	private final Optional<SslHandlerFactory> sslHandlerFactory;
	private final ServiceChannelInitializer serviceChannelInitializer;
	private final ServerConfig config;
	
	@Inject
	public ServerTransportProvider(Optional<NativeIO> nativeIO, EventLoopGroup eventLoopGroup, @StartStopLoop EventLoop startStopLoop,
			ChannelFactory<ServerChannel> channelFactory, Optional<SslHandlerFactory> sslHandlerFactory, 
			ServiceChannelInitializer serviceChannelInitializer, ServerConfig config) {
		this.nativeIO=nativeIO;
		this.eventLoopGroup=eventLoopGroup;
		this.startStopLoop=startStopLoop;
		this.channelFactory=channelFactory;
		this.sslHandlerFactory=sslHandlerFactory;
		this.serviceChannelInitializer=serviceChannelInitializer;
		this.config=config;
	}

	@Override
	public ServerTransport get() {
		return nativeIO.map(n->n.transport(eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config))
				.orElseGet(()->new NioServerTransport(eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config));
	}

}
