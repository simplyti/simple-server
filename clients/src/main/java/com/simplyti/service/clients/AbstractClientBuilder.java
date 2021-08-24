package com.simplyti.service.clients;

import java.util.concurrent.ThreadFactory;

import com.simplyti.service.clients.channel.factory.DefaultChannelFactory;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.monitor.DefaultClientMonitor;
import com.simplyti.service.clients.request.BaseClientRequestBuilder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.concurrent.DefaultThreadFactory;

public abstract class AbstractClientBuilder<B,T extends Client<R>,R extends BaseClientRequestBuilder<R>> implements ClientBuilder<B,T,R>, EventLoopGroupFactory {
	
	private final String name;
	
	private EventLoopGroup eventLoopGroup;
	private EventLoopGroupFactory eventLoopGroupFactory;
	private ChannelFactory<Channel> channelFactory;
	private Endpoint endpoint;
	private SslProvider sslProvider;
	private boolean monitorEnabled;
	private int poolSize;
	private boolean unpooledChannels;
	private long poolIdleTimeout;
	private long readTimeoutMilis;
	private boolean verbose;
	
	public AbstractClientBuilder() {
		this("client");
	}
	
	public AbstractClientBuilder(String name) {
		this.name=name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B withEventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup=eventLoopGroup;
		return (B) this;
	}
	
	@Override
	public B eventLoopGroup(EventLoopGroup eventLoopGroup) {
		return withEventLoopGroup(eventLoopGroup);
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
	public B withMonitorEnabled() {
		return withMonitorEnabled(true);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B withMonitorEnabled(boolean enabled) {
		this.monitorEnabled=enabled;
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
	@SuppressWarnings("unchecked")
	public B withReadTimeout(long timeoutMillis) {
		this.readTimeoutMilis = timeoutMillis;
		return (B) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B verbose() {
		this.verbose = true;
		return (B) this;
	}
	
	@Override
	public T build() {
		EventLoopGroup elg = eventLoopGroup();
		return build0(elg, bootstrap(elg), endpoint, sslProvider(), monitorEnabled?new DefaultClientMonitor(elg):null,poolSize,unpooledChannels, poolIdleTimeout, readTimeoutMilis, verbose);
	}

	private SslProvider sslProvider() {
		return sslProvider;
	}

	private BootstrapProvider bootstrap(EventLoopGroup eventLoopGroup) {
		return new BootstrapProvider(eventLoopGroup,channelFactory(eventLoopGroup));
	}

	private ChannelFactory<Channel> channelFactory(EventLoopGroup eventLoopGroup) {
		if(channelFactory!=null) {
			return channelFactory;
		} else {
			return new DefaultChannelFactory(eventLoopGroup);
		}
	}

	protected abstract T build0(EventLoopGroup eventLoopGroup, BootstrapProvider bootstrap, Endpoint endpoint, SslProvider sslProvider, DefaultClientMonitor monitor, 
			int poolSize, boolean unpooledChannels, long poolIdleTimeout, long readTimeoutMilis,
			boolean verbose);
	
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
		ThreadFactory threadFactory = new DefaultThreadFactory(name+"-pool", true);
		if(Epoll.isAvailable()) {
			return new EpollEventLoopGroup(threadFactory);
		}else if(KQueue.isAvailable()) {
			return new KQueueEventLoopGroup(threadFactory);
		}else {
			return new NioEventLoopGroup(threadFactory);
		}
	}
	
}
