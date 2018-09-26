package com.simplyti.service.clients;


import com.simplyti.service.clients.channel.monitor.ClientMonitor;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.util.concurrent.Future;

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
	
	@Override
	public Future<Void> close() {
		return internalClient.close();
	}

}
