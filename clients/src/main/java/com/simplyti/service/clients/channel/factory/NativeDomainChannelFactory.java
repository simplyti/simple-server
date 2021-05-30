package com.simplyti.service.clients.channel.factory;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueueDomainSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.unix.DomainSocketChannel;

public class NativeDomainChannelFactory implements ChannelFactory<DomainSocketChannel> {

	private final EventLoopGroup eventLoopGroup;

	public NativeDomainChannelFactory(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup=eventLoopGroup;
	}

	@Override
	public DomainSocketChannel newChannel() {
		if(eventLoopGroup instanceof EpollEventLoopGroup) {
			return new EpollDomainSocketChannel();
		}else if(eventLoopGroup instanceof KQueueEventLoopGroup) {
			return new KQueueDomainSocketChannel();
		}else {
			return null;
		}
	}

}
