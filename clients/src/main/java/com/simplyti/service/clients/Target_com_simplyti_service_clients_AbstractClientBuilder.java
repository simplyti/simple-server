package com.simplyti.service.clients;

import com.simplyti.service.clients.channel.factory.NioChannelFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslProvider;

final class Target_com_simplyti_service_clients_AbstractClientBuilder {
	
	public EventLoopGroup get() {
        return new NioEventLoopGroup();
    }
	
	@SuppressWarnings("unused")
	private SslProvider sslProvider() {
		return SslProvider.JDK;
	}
	
	@SuppressWarnings("unused")
	private ChannelFactory<Channel> channelFactory(EventLoopGroup eventLoopGroup) {
		return new NioChannelFactory();
	}

}
