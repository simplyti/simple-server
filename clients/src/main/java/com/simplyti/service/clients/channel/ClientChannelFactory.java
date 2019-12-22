package com.simplyti.service.clients.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;

public class ClientChannelFactory implements ChannelFactory<Channel> {

	private final NioChannelFactory nioChannelFactory;
	private final NativeChannelFactory nativeChannelFactory;

	public ClientChannelFactory(EventLoopGroup eventLoopGroup) {
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
