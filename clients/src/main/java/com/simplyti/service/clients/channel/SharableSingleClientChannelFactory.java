package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.BootstrapProvider;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.monitor.ClientMonitorHandler;
import com.simplyti.util.concurrent.Future;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslProvider;

public class SharableSingleClientChannelFactory implements ClientChannelFactory { 
	
	private final FixedSizeClientChannelFactory fixed;
	
	public SharableSingleClientChannelFactory(BootstrapProvider bootstrap, EventLoopGroup eventLoopGroup, ChannelPoolHandler handler, SslProvider sslProvider, ClientMonitorHandler monitor) {
		this.fixed = new FixedSizeClientChannelFactory(bootstrap, eventLoopGroup, handler, sslProvider, monitor, 1);
	}

	@Override
	public Future<ClientChannel> channel(Endpoint endpoint, long responseTimeoutMillis) {
		return fixed.channel(endpoint,responseTimeoutMillis)
				.thenApply(this::release);
	}
	
	private ClientChannel release(ClientChannel channel) {
		channel.release();
		return channel;
	}


}
