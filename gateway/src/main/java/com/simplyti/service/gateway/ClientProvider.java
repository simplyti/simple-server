package com.simplyti.service.gateway;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.PoolConfig;
import com.simplyti.service.gateway.channel.ProxyClientChannelPoolHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class ClientProvider implements Provider<InternalClient>{

	private final EventLoopGroup eventLoopGroup;
	private final GatewayConfig config;
	private final ChannelFactory<Channel> channelFactory;
	
	@Override
	public InternalClient get() {
		return new InternalClient(eventLoopGroup,new ProxyClientChannelPoolHandler(),channelFactory,new PoolConfig(config.maxIddle(), null));
	}

}
