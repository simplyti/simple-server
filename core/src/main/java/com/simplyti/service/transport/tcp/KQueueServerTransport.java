package com.simplyti.service.transport.tcp;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.ssl.SslHandlerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public class KQueueServerTransport extends AbstractTcpServerTransport {
	
	@Inject
	public KQueueServerTransport(Provider<EventLoopGroup> eventLoopGroup, @StartStopLoop Provider<EventLoop> startStopLoop,
			ChannelFactory<ServerChannel> channelFactory, Optional<SslHandlerFactory> sslHandlerFactory, 
			ServiceChannelInitializer serviceChannelInitializer, ServerConfig config) {
		super(eventLoopGroup,startStopLoop,channelFactory,sslHandlerFactory,serviceChannelInitializer,config);
	}

	@Override
	protected void configure(ServerBootstrap bootstrap) {
		
	}

}
