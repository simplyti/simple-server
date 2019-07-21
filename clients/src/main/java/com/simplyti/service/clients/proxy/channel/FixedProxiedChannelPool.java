package com.simplyti.service.clients.proxy.channel;

import com.simplyti.service.clients.proxy.Proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class FixedProxiedChannelPool extends FixedChannelPool {

	private ProxyHandlerInitializer proxyHandlerInitializer;

	public FixedProxiedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, Proxy proxy, ChannelHealthChecker check, int poolSize) {
		super(bootstrap, handler,check,null, -1, poolSize,Integer.MAX_VALUE);
		this.proxyHandlerInitializer=new ProxyHandlerInitializer(handler,proxy);
	}

	@Override
	protected ChannelFuture connectChannel(Bootstrap bs) {
		return bs.handler(proxyHandlerInitializer).connect();
	}

}
