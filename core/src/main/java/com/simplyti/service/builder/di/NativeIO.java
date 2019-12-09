package com.simplyti.service.builder.di;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;

public class NativeIO {

	public ServerChannel serverChannel() {
		if(Epoll.isAvailable()) {
			return new EpollServerSocketChannel();
		}else if(KQueue.isAvailable()) {
			return new KQueueServerSocketChannel();
		}else {
			return null;
		}
	}

	public EventLoopGroup eventLoopGroup(int size) {
		if(Epoll.isAvailable()) {
			return new EpollEventLoopGroup(size);
		}else if(KQueue.isAvailable()) {
			return new KQueueEventLoopGroup(size);
		}else {
			return null;
		}
	}

}
