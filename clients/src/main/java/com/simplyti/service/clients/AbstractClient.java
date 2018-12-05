package com.simplyti.service.clients;


import com.simplyti.service.clients.channel.monitor.ClientMonitor;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.util.concurrent.Future;

public abstract class AbstractClient<B extends ClientRequestBuilder<B>> implements Client<B> {
	
	private final InternalClient internalClient;
	
	public AbstractClient(EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler) {
		this(eventLoopGroup,poolHandler,null);
	}

	public AbstractClient(EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler, PoolConfig poolConfig) {
		this.internalClient = new InternalClient(eventLoopGroup, poolHandler, poolConfig);
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
		return internalClient.closeIdleClannels();
	}

}
