package com.simplyti.service.builder.di;

import java.util.Optional;

import javax.inject.Provider;

import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.ssl.SslHandlerFactory;
import com.simplyti.service.transport.ServerTransport;
import com.simplyti.service.transport.tcp.EpollServerTransport;
import com.simplyti.service.transport.tcp.KQueueServerTransport;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.unix.ServerDomainSocketChannel;

public class NativeIO {
	
	public ServerDomainSocketChannel serverDomainChannel() {
		if(Epoll.isAvailable()) {
			return new EpollServerDomainSocketChannel();
		} else if(KQueue.isAvailable()) {
			return new KQueueServerDomainSocketChannel();
		} else {
			return null;
		}
	}

	public ServerChannel serverChannel() {
		if(Epoll.isAvailable()) {
			return new EpollServerSocketChannel();
		} else if(KQueue.isAvailable()) {
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

	public ServerTransport tcpTransport(Provider<EventLoopGroup> eventLoopGroup, @StartStopLoop Provider<EventLoop> startStopLoop,
			ChannelFactory<ServerChannel> channelFactory, Optional<SslHandlerFactory> sslHandlerFactory, 
			ServiceChannelInitializer serviceChannelInitializer, ServerConfig config) {
		if(Epoll.isAvailable()) {
			return new EpollServerTransport(eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config);
		}else if(KQueue.isAvailable()) {
			return new KQueueServerTransport(eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config);
		}else {
			return null;
		}
	}

}
