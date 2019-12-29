package com.simplyti.service.clients.channel.proxy;

import com.simplyti.service.clients.proxy.Proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;

public class FixedProxiedChannelPool extends FixedChannelPool {

	private final ProxyHandlerInitializer proxyHandlerInitializer;

	public FixedProxiedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int size, Proxy proxy) {
		super(bootstrap,handler,size);
		this.proxyHandlerInitializer=new ProxyHandlerInitializer(handler,proxy);
	}

	@Override
	protected ChannelFuture connectChannel(Bootstrap bs) {
		return bs.handler(proxyHandlerInitializer).connect();
	}

}
