package com.simplyti.service.clients.channel.factory;

import java.nio.channels.spi.SelectorProvider;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NioChannelFactory implements ChannelFactory<Channel> {

	@Override
	public Channel newChannel() {
		return new NioSocketChannel(SelectorProvider.provider());
	}

}
