package com.simplyti.service.clients.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;

public class SimpleDirectChannelPool extends SimpleChannelPool {
	
	public SimpleDirectChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler) {
		super(bootstrap, handler);
	}
	
	@Override
	protected ChannelFuture connectChannel(Bootstrap bs) {
		return bs.connect();
	}

}
