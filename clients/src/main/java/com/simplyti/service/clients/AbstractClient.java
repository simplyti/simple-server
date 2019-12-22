package com.simplyti.service.clients;


import com.simplyti.service.clients.channel.monitor.ClientMonitor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.concurrent.Future;

public abstract class AbstractClient<B extends ClientRequestBuilder<B>> implements Client<B> {
	
	private final InternalClient internalClient;
	
	public AbstractClient(EventLoopGroup eventLoopGroup, ChannelFactory<Channel> channelFactory, ChannelPoolHandler poolHandler) {
		this(eventLoopGroup,poolHandler,channelFactory, null);
	}
	
	public AbstractClient(EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler, ChannelFactory<Channel> channelFactory, PoolConfig poolConfig) {
		this(null,eventLoopGroup,poolHandler,channelFactory, poolConfig);
	}

	public AbstractClient(SslProvider sslProvider, EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler, ChannelFactory<Channel> channelFactory, PoolConfig poolConfig) {
		this.internalClient = new InternalClient(sslProvider, eventLoopGroup, poolHandler, channelFactory, poolConfig);
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
