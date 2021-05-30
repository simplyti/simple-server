package com.simplyti.service.builder.di;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.ssl.SslHandlerFactory;
import com.simplyti.service.transport.ServerTransport;
import com.simplyti.service.transport.tcp.NioServerTransport;
import com.simplyti.service.transport.unix.UnixDomainTransport;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.unix.ServerDomainSocketChannel;

public class ServerTransportProvider implements Provider<Set<ServerTransport>>{
	
	private final Provider<Optional<NativeIO>> nativeIO;
	private final Provider<EventLoopGroup> eventLoopGroup;
	private final Provider<EventLoop> startStopLoop;
	private final ChannelFactory<ServerChannel> channelFactory;
	private final ChannelFactory<ServerDomainSocketChannel> domainChannelFactory;
	private final Optional<SslHandlerFactory> sslHandlerFactory;
	private final ServiceChannelInitializer serviceChannelInitializer;
	private final ServerConfig config;
	
	@Inject
	public ServerTransportProvider(Provider<Optional<NativeIO>> nativeIO, Provider<EventLoopGroup> eventLoopGroup, @StartStopLoop Provider<EventLoop> startStopLoop,
			ChannelFactory<ServerChannel> channelFactory, ChannelFactory<ServerDomainSocketChannel> domainChannelFactory,
			Optional<SslHandlerFactory> sslHandlerFactory, 
			ServiceChannelInitializer serviceChannelInitializer, ServerConfig config) {
		this.nativeIO=nativeIO;
		this.eventLoopGroup=eventLoopGroup;
		this.startStopLoop=startStopLoop;
		this.channelFactory=channelFactory;
		this.domainChannelFactory=domainChannelFactory;
		this.sslHandlerFactory=sslHandlerFactory;
		this.serviceChannelInitializer=serviceChannelInitializer;
		this.config=config;
	}

	@Override
	public Set<ServerTransport> get() {
		ServerTransport tcpTransport = nativeIO.get().map(n->n.tcpTransport(eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config))
				.orElseGet(()->new NioServerTransport(eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config));
		
		ServerTransport domainTransport = new UnixDomainTransport(eventLoopGroup, startStopLoop, domainChannelFactory, serviceChannelInitializer, config);
		
		return new HashSet<>(Arrays.asList(tcpTransport,domainTransport));
	}

}
