package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.PoolConfig;
import com.simplyti.service.clients.proxy.channel.FixedProxiedChannelPool;
import com.simplyti.service.clients.proxy.channel.NoResolvingSocketAddress;
import com.simplyti.service.clients.proxy.channel.SimpleProxiedChannelPool;
import com.simplyti.service.clients.ssl.SSLChannelInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;

public class SimpleChannelPoolMap extends AbstractChannelPoolMap<Endpoint, ChannelPool> {
	
	private final Bootstrap bootstrap;
	private final ChannelPoolHandler initializer;
	private final ChannelHealthChecker checker;
	private final PoolConfig pool;
	
	public SimpleChannelPoolMap(EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler, ChannelHealthChecker checker,
			PoolConfig pool) {
		this.bootstrap = new Bootstrap().group(eventLoopGroup)
				.channelFactory(new ClientChannelFactory(eventLoopGroup))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		this.initializer = poolHandler;
		this.checker=checker;
		this.pool=pool;
	}

	@Override
	protected ChannelPool newPool(Endpoint key) {
		if(pool!=null && pool.poolSize()!=null) {
			return newFixedPool(key,pool.poolSize());
		}else {
			return newSimplePool(key);
		}
	}
	
	private ChannelPool newFixedPool(Endpoint key, int poolSize) {
		if(key.isProxied()) {
			return new FixedProxiedChannelPool(bootstrap.clone().remoteAddress(new NoResolvingSocketAddress(key.address())), handler(key), key.asProxied().proxy(),checker,poolSize);
		}else {
			return new FixedChannelPool(bootstrap.clone().remoteAddress(key.address().host(), key.address().port()), handler(key), checker, null, -1, poolSize,Integer.MAX_VALUE);
		}
	}

	private ChannelPool newSimplePool(Endpoint key) {
		if(key.isProxied()) {
			return new SimpleProxiedChannelPool(bootstrap.clone().remoteAddress(new NoResolvingSocketAddress(key.address())), handler(key), key.asProxied().proxy(),checker);
		}else {
			return new SimpleChannelPool(bootstrap.clone().remoteAddress(key.address().host(),key.address().port()), handler(key),checker);
		}
	}

	private ChannelPoolHandler handler(Endpoint key) {
		if(key.schema().ssl()) {
			return new SSLChannelInitializer(initializer,key);
		}else {
			return initializer;
		}
	}

}
