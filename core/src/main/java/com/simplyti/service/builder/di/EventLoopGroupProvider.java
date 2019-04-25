package com.simplyti.service.builder.di;

import javax.inject.Provider;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class EventLoopGroupProvider implements Provider<EventLoopGroup>{
	
	@Override
	public EventLoopGroup get() {
		if(Epoll.isAvailable()) {
			return new EpollEventLoopGroup();
		}else if(KQueue.isAvailable()) {
			return new KQueueEventLoopGroup();
		}else {
			return new NioEventLoopGroup();
		}
	}

}
