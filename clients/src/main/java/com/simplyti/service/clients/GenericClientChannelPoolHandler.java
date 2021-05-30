package com.simplyti.service.clients;

import java.util.function.Consumer;

import io.netty.channel.Channel;

public class GenericClientChannelPoolHandler extends AbstractClientPoolHandler {

	private final Consumer<Channel> customInit;

	public GenericClientChannelPoolHandler(long readTimeoutMillis, boolean verbose, Consumer<Channel> customInit) {
		super(readTimeoutMillis,verbose);
		this.customInit=customInit;
	}

	@Override
	public void channelCreated0(Channel ch) {
		if(customInit!=null) {
			customInit.accept(ch);
		}
	}
	
}
