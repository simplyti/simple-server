package com.simplyti.service.clients;


import com.simplyti.service.clients.channel.monitor.ClientMonitor;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;

public abstract class AbstractClient<B extends ClientRequestBuilder<B>> implements Client<B> {
	
	private final InternalClient internalClient;

	public AbstractClient(EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler) {
		this.internalClient = new InternalClient(eventLoopGroup, poolHandler);
	}

	@Override
	public ClientMonitor monitor() {
		return internalClient;
	}
	
	protected InternalClient client() {
		return internalClient;
	}

}
