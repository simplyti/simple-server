package com.simplyti.service.clients.channel.factory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;

public class NativeChannelFactory implements ChannelFactory<Channel> {

	private final EventLoopGroup eventLoopGroup;

	public NativeChannelFactory(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup=eventLoopGroup;
	}

	@Override
	public Channel newChannel() {
		if(eventLoopGroup instanceof EpollEventLoopGroup) {
			return new EpollSocketChannel();
		}else if(eventLoopGroup instanceof KQueueEventLoopGroup) {
			return new KQueueSocketChannel();
		}else {
			return null;
		}
	}

}
