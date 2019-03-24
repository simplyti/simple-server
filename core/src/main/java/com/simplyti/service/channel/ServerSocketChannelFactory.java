package com.simplyti.service.channel;

import java.nio.channels.spi.SelectorProvider;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerSocketChannelFactory implements ChannelFactory<ServerChannel> {

	private final EventLoopGroup eventLoopGroup;

	public ServerSocketChannelFactory(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup=eventLoopGroup;
	}

	@Override
	public ServerChannel newChannel() {
		if(eventLoopGroup instanceof EpollEventLoopGroup) {
			return new EpollServerSocketChannel();
		}else if(eventLoopGroup instanceof KQueueEventLoopGroup) {
			return new KQueueServerSocketChannel();
		}else {
			return new NioServerSocketChannel(SelectorProvider.provider());
		}
	}

}
