package com.simplyti.service.gateway;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.clients.Client;
import com.simplyti.service.clients.GenericClient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class ClientProvider implements Provider<GenericClient>{

	private final EventLoopGroup eventLoopGroup;
	private final GatewayConfig config;
	private final ChannelFactory<Channel> channelFactory;
	
	@Override
	public GenericClient get() {
		return Client.builder()
			.withEventLoopGroup(eventLoopGroup)
			.withChannelFactory(channelFactory)
			.withChannelPoolIdleTimeout(config.maxChannelIdleTimeout())
			.build();
	}

}
