package com.simplyti.service.builder.di;

import java.nio.channels.spi.SelectorProvider;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.ssl.SslHandlerFactory;
import com.simplyti.service.transport.ServerTransport;
import com.simplyti.service.transport.tcp.EpollServerTransport;
import com.simplyti.service.transport.tcp.KQueueServerTransport;
import com.simplyti.service.transport.tcp.NioServerTransport;

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
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.ServerDomainSocketChannel;

public class NativeIO {
	
	private final Optional<EventLoopGroup> provided;

	@Inject
	public NativeIO(@BuiltProvided Optional<EventLoopGroup> provided) {
		this.provided = provided;
	}
	
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
		if(provided.isPresent()) {
			return serverChannelForClass(provided.get());
		}
		
		if(Epoll.isAvailable()) {
			return new EpollServerSocketChannel();
		} else if(KQueue.isAvailable()) {
			return new KQueueServerSocketChannel();
		}else {
			return new NioServerSocketChannel(SelectorProvider.provider());
		}
	}

	private ServerChannel serverChannelForClass(EventLoopGroup eventLoopGroup) {
		if(eventLoopGroup instanceof EpollEventLoopGroup) {
			return new EpollServerSocketChannel();
		} else if (eventLoopGroup instanceof KQueueEventLoopGroup) {
			return new KQueueServerSocketChannel();
		} else {
			return new NioServerSocketChannel();
		}
	}

	public EventLoopGroup eventLoopGroup(int size) {
		if(provided.isPresent()) {
			return eventLoopGroupForClass(provided.get(),size);
		}
		
		if(Epoll.isAvailable()) {
			return new EpollEventLoopGroup(size);
		} else if(KQueue.isAvailable()) {
			return new KQueueEventLoopGroup(size);
		} else {
			return new NioEventLoopGroup(size);
		}
	}

	private EventLoopGroup eventLoopGroupForClass(EventLoopGroup eventLoopGroup, int size) {
		if(eventLoopGroup instanceof EpollEventLoopGroup) {
			return new EpollEventLoopGroup(size);
		} else if (eventLoopGroup instanceof KQueueEventLoopGroup) {
			return new KQueueEventLoopGroup(size);
		} else {
			return new NioEventLoopGroup(size);
		}
	}

	public ServerTransport tcpTransport(Provider<EventLoopGroup> eventLoopGroup, @StartStopLoop Provider<EventLoop> startStopLoop,
			ChannelFactory<ServerChannel> channelFactory, Optional<SslHandlerFactory> sslHandlerFactory, 
			ServiceChannelInitializer serviceChannelInitializer, ServerConfig config) {
		if(provided.isPresent()) {
			return tcpTransportForClass(provided.get(), startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config);
		}
		
		if(Epoll.isAvailable()) {
			return new EpollServerTransport(eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config);
		}else if(KQueue.isAvailable()) {
			return new KQueueServerTransport(eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config);
		}else {
			return new NioServerTransport(eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config);
		}
	}

	private ServerTransport tcpTransportForClass(EventLoopGroup eventLoopGroup, Provider<EventLoop> startStopLoop,
			ChannelFactory<ServerChannel> channelFactory, Optional<SslHandlerFactory> sslHandlerFactory,
			ServiceChannelInitializer serviceChannelInitializer, ServerConfig config) {
		if(eventLoopGroup instanceof EpollEventLoopGroup) {
			return new EpollServerTransport(()->eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config);
		} else if (eventLoopGroup instanceof KQueueEventLoopGroup) {
			return new KQueueServerTransport(()->eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config);
		}else {
			return new NioServerTransport(()->eventLoopGroup, startStopLoop, channelFactory, sslHandlerFactory, serviceChannelInitializer, config);
		}
	}

}
