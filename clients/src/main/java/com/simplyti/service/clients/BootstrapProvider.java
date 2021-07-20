package com.simplyti.service.clients;

import com.simplyti.service.clients.channel.factory.DefaultDomainChannelFactory;
import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.service.clients.endpoint.UnixAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

public class BootstrapProvider {

	private final EventLoopGroup eventLoopGroup;
	private final ChannelFactory<Channel> channelFactory;
	
	private Bootstrap tcpBootstrap;
	private Bootstrap unixBootstrap;

	public BootstrapProvider(EventLoopGroup eventLoopGroup, ChannelFactory<Channel> channelFactory) {
		this.eventLoopGroup=eventLoopGroup;
		this.channelFactory=channelFactory;
	}

	public Bootstrap get(Address address) {
		if(address instanceof UnixAddress) {
			return getUnix();
		} else {
			return getTcp();
		}
	}

	private Bootstrap getUnix() {
		if(this.unixBootstrap !=null) {
			return this.unixBootstrap;
		}
		this.unixBootstrap = new Bootstrap().group(eventLoopGroup)
				.channelFactory(new DefaultDomainChannelFactory(eventLoopGroup))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		return this.unixBootstrap;
	}

	private Bootstrap getTcp() {
		if(this.tcpBootstrap !=null) {
			return this.tcpBootstrap;
		}
		this.tcpBootstrap = new Bootstrap().group(eventLoopGroup)
				.channelFactory(channelFactory)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_REUSEADDR, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		return this.tcpBootstrap;
	}

}
