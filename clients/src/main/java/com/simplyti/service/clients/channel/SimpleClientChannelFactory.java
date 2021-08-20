package com.simplyti.service.clients.channel;


import com.simplyti.service.clients.BootstrapProvider;
import com.simplyti.service.clients.channel.proxy.SimpleProxiedChannelPool;
import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.service.clients.monitor.ClientMonitorHandler;
import com.simplyti.service.clients.proxy.Proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslProvider;

public class SimpleClientChannelFactory extends AbstractClientChannelPoolMap {
	
	public SimpleClientChannelFactory(BootstrapProvider bootstrap, EventLoopGroup eventLoopGroup, ChannelPoolHandler handler, SslProvider sslProvider, ClientMonitorHandler monitorHandler) {
		super(bootstrap,eventLoopGroup,handler, sslProvider,monitorHandler);
	}
	
	@Override
	protected ChannelPool newPool(Bootstrap bootstrap, ChannelPoolHandler handler, Address address, Proxy proxy) {
		if(proxy!=null) {
			return new SimpleProxiedChannelPool(bootstrap, handler, proxy);
		} else {
			return new SimpleDirectChannelPool(bootstrap, handler);
		}
	}
	
}
