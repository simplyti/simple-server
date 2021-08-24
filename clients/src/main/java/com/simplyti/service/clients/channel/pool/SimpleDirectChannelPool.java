package com.simplyti.service.clients.channel.pool;

import com.simplyti.service.clients.channel.handler.DirectHandlerInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;

public class SimpleDirectChannelPool extends SimpleChannelPool {
	
	private DirectHandlerInitializer initializer;

	public SimpleDirectChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler) {
		super(bootstrap, handler);
		this.initializer=new DirectHandlerInitializer(handler);
	}
	
	@Override
	protected ChannelFuture connectChannel(Bootstrap bs) {
		return bs.handler(initializer).connect();
	}

}
