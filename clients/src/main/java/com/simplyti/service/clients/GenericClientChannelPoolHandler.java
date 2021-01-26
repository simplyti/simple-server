package com.simplyti.service.clients;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class GenericClientChannelPoolHandler extends AbstractChannelPoolHandler {

	private final long readTimeoutMillis;
	private final Consumer<Channel> customInit;

	public GenericClientChannelPoolHandler(long readTimeoutMillis, Consumer<Channel> customInit) {
		this.readTimeoutMillis=readTimeoutMillis;
		this.customInit=customInit;
	}

	@Override
	public void channelCreated(Channel ch) throws Exception {
		if(readTimeoutMillis >0) {
			ch.pipeline().addLast(new ReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS));
		}
		if(customInit!=null) {
			customInit.accept(ch);
		}
	}
	
}
