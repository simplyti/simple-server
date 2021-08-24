package com.simplyti.service.clients.channel.factory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;

public class DefaultChannelFactory implements ChannelFactory<Channel> {

	private final NioChannelFactory nioChannelFactory;
	private final NativeChannelFactory nativeChannelFactory;

	public DefaultChannelFactory(EventLoopGroup eventLoopGroup) {
		this.nioChannelFactory = new NioChannelFactory();
		this.nativeChannelFactory = new NativeChannelFactory(eventLoopGroup);
	}

	@Override
	public Channel newChannel() {
		Channel channel = this.nativeChannelFactory.newChannel();
		if(channel!=null) {
			return channel;
		} else {
			return nioChannelFactory.newChannel();
		}
	}
	
}