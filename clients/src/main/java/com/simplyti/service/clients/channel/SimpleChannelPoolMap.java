package com.simplyti.service.clients.channel;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.proxy.channel.NoResolvingSocketAddress;
import com.simplyti.service.clients.proxy.channel.ProxiedChannelPool;
import com.simplyti.service.clients.ssl.SSLChannelInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SimpleChannelPoolMap extends AbstractChannelPoolMap<Endpoint, ChannelPool> {
	
	private final Bootstrap bootstrap;
	private final ChannelPoolHandler initializer;

	public SimpleChannelPoolMap(EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler) {
		this.bootstrap = new Bootstrap().group(eventLoopGroup)
				.channel(channelClass(eventLoopGroup))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		this.initializer = poolHandler;
	}

	@Override
	protected ChannelPool newPool(Endpoint key) {
		if(key.isProxied()) {
			return new ProxiedChannelPool(bootstrap.clone().remoteAddress(new NoResolvingSocketAddress(key.address())), handler(key), key.asProxied().proxy());
		}else {
			return new SimpleChannelPool(bootstrap.clone().remoteAddress(key.address().host(),key.address().port()), handler(key));
		}
	}
	
	private ChannelPoolHandler handler(Endpoint key) {
		if(key.schema().ssl()) {
			return new SSLChannelInitializer(initializer,key.address());
		}else {
			return initializer;
		}
	}

	private Class<? extends Channel> channelClass(EventLoopGroup eventLoopGroup) {
		return Match(eventLoopGroup).of(
				Case($(instanceOf(EpollEventLoopGroup.class)), EpollSocketChannel.class),
				Case($(), NioSocketChannel.class));
	}

}
