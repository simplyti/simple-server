package com.simplyti.service.client;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientChannelPool extends AbstractChannelPoolMap<ServerAddress, ChannelPool>{
	
	private final Bootstrap bootstrap;
	private final ClientChannelInitializer initializer;
	private final ChannelGroup channelGroup;

	public ClientChannelPool(EventLoopGroup eventLoopGroup,ServerCertificateHandler serverCertificateHandler) {
		this.bootstrap = new Bootstrap().group(eventLoopGroup)
				.channel(channelClass(eventLoopGroup))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		this.channelGroup=new DefaultChannelGroup(eventLoopGroup.next());
		this.initializer = new  ClientChannelInitializer(channelGroup,serverCertificateHandler);
	}

	@Override
	protected ChannelPool newPool(ServerAddress key) {
		if(key.isSsl()) {
			return new SimpleChannelPool(bootstrap.clone().remoteAddress(key.getHost(),key.getPort()), 
					new SSLClientChannelInitializer(key.getSni(),key.getPort(),initializer));
		}else {
			return new SimpleChannelPool(bootstrap.clone().remoteAddress(key.getHost(),key.getPort()), 
					initializer);
		}
	}
	
	private Class<? extends Channel> channelClass(EventLoopGroup eventLoopGroup) {
		return Match(eventLoopGroup).of(
				Case($(instanceOf(EpollEventLoopGroup.class)), EpollSocketChannel.class),
				Case($(), NioSocketChannel.class));
	}

	public int activeConnections() {
		return channelGroup.size();
	}
	
}
