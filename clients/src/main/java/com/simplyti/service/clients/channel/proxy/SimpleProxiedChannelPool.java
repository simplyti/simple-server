package com.simplyti.service.clients.channel.proxy;

import com.simplyti.service.clients.proxy.Proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;

public class SimpleProxiedChannelPool extends SimpleChannelPool {

	private ProxyHandlerInitializer proxyHandlerInitializer;

	public SimpleProxiedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, Proxy proxy) {
		super(bootstrap, handler);
		this.proxyHandlerInitializer=new ProxyHandlerInitializer(handler,proxy);
	}

	@Override
	protected ChannelFuture connectChannel(Bootstrap bs) {
		return bs.handler(proxyHandlerInitializer).connect();
	}

}