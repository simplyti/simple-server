package com.simplyti.service.clients.channel.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.pool.ChannelPoolHandler;

public class DirectHandlerInitializer extends ChannelInitializer<Channel> {
	
	private ChannelPoolHandler handler;

	public DirectHandlerInitializer(ChannelPoolHandler handler) {
		this.handler=handler;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast(new PrematureErrorHandler());
		handler.channelCreated(ch);
	}

}
