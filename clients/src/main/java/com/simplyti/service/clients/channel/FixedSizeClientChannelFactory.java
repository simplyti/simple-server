package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.channel.proxy.FixedProxiedChannelPool;
import com.simplyti.service.clients.monitor.ClientMonitorHandler;
import com.simplyti.service.clients.proxy.Proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.handler.ssl.SslProvider;

public class FixedSizeClientChannelFactory extends AbstractClientChannelPoolMap  {
	
	private final int size;

	public FixedSizeClientChannelFactory(Bootstrap bootstrap, EventLoopGroup eventLoopGroup, ChannelPoolHandler handler, SslProvider sslProvider, ClientMonitorHandler monitor, int size) {
		super(bootstrap,eventLoopGroup,handler,sslProvider,monitor);
		this.size=size;
	}
	
	@Override
	protected ChannelPool newPool(Bootstrap bootstrap, ChannelPoolHandler handler, Proxy proxy) {
		if(proxy!=null) {
			return new FixedProxiedChannelPool(bootstrap, handler, size, proxy);
		} else {
			return new FixedChannelPool(bootstrap, handler, size);
		}
	}


}
