package com.simplyti.service.clients;


import com.simplyti.service.clients.channel.factory.DefaultChannelFactory;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.monitor.DefaultClientMonitor;
import com.simplyti.service.clients.request.BaseClientRequestBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslProvider;

public abstract class AbstractClientBuilder<B,T extends Client<R>,R extends BaseClientRequestBuilder<R>> implements ClientBuilder<B,T,R>, EventLoopGroupFactory {
	
	private EventLoopGroup eventLoopGroup;
	private EventLoopGroupFactory eventLoopGroupFactory;
	private ChannelFactory<Channel> channelFactory;
	private Endpoint endpoint;
	private SslProvider sslProvider;
	private boolean monitorEnabled;
	private int poolSize;
	private boolean unpooledChannels;
	private long poolIdleTimeout;

	@Override
	@SuppressWarnings("unchecked")
	public B withEventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup=eventLoopGroup;
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withEventLoopGroupFactory(EventLoopGroupFactory eventLoopGroupFactory) {
		this.eventLoopGroupFactory=eventLoopGroupFactory;
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withChannelFactory(ChannelFactory<Channel> channelFactory) {
		this.channelFactory=channelFactory;
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withEndpoint(String host, int port) {
		this.endpoint=new Endpoint(null,host, port);
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withEndpoint(Endpoint endpoint) {
		this.endpoint=endpoint;
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withSslProvider(SslProvider sslProvider) {
		this.sslProvider=sslProvider;
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withMonitorEnabled() {
		this.monitorEnabled=true;
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withChannelPoolSize(int poolSize) {
		this.poolSize=poolSize;
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withUnpooledChannels() {
		this.unpooledChannels=true;
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withChannelPoolIdleTimeout(long poolIdleTimeout) {
		this.poolIdleTimeout=poolIdleTimeout;
		return (B) this;
	}
	
	@Override
	public T build() {
		EventLoopGroup elg = eventLoopGroup();
		return build0(elg, bootstrap(),endpoint, sslProvider, monitorEnabled?new DefaultClientMonitor(elg):null,poolSize,unpooledChannels, poolIdleTimeout);
	}

	private Bootstrap bootstrap() {
		return new Bootstrap().group(eventLoopGroup)
			.channelFactory(channelFactory())
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
			.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
	}

	private ChannelFactory<Channel> channelFactory() {
		if(channelFactory!=null) {
			return channelFactory;
		} else {
			return new DefaultChannelFactory(eventLoopGroup);
		}
	}

	protected abstract T build0(EventLoopGroup eventLoopGroup, Bootstrap bootstrap, Endpoint endpoint, SslProvider sslProvider, DefaultClientMonitor monitor, 
			int poolSize, boolean unpooledChannels, long poolIdleTimeout);
	
	private EventLoopGroup eventLoopGroup() {
		if(eventLoopGroup!=null) {
			return eventLoopGroup;
		} else {
			return eventLoopGroupFactory().get();
		}
	}

	private EventLoopGroupFactory eventLoopGroupFactory() {
		if(eventLoopGroupFactory!=null) {
			return eventLoopGroupFactory;
		} else {
			return this;
		}
	}
	
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
