package com.simplyti.service.gateway.channel;
import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.clients.channel.factory.DefaultChannelFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class ChannelFactoryProvider implements Provider<ChannelFactory<Channel>>{

	private final EventLoopGroup eventLoopGroup;
	@Override
	public ChannelFactory<Channel> get() {
		return new DefaultChannelFactory(eventLoopGroup);
	}

}
