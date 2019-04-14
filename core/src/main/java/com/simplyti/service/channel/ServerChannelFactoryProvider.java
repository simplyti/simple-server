package com.simplyti.service.channel;

import javax.inject.Inject;
import javax.inject.Provider;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class ServerChannelFactoryProvider implements Provider<ChannelFactory<ServerChannel>>{

	private final EventLoopGroup eventLoopGroup;
	
	@Override
	public ChannelFactory<ServerChannel> get() {
		return new ServerSocketChannelFactory(eventLoopGroup);
	}

}
