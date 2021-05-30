package com.simplyti.service.clients;

import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.channel.FixedSizeClientChannelFactory;
import com.simplyti.service.clients.channel.SimpleClientChannelFactory;
import com.simplyti.service.clients.channel.SimpleMultiplexedClientChannelFactory;
import com.simplyti.service.clients.channel.UnpooledClientChannelFactory;
import com.simplyti.service.clients.channel.handler.IdleTimeoutHandler;
import com.simplyti.service.clients.monitor.ClientMonitor;
import com.simplyti.service.clients.monitor.ClientMonitorHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.concurrent.Future;

public abstract class AbstractClient<T> implements Client<T> {

	private final ClientChannelFactory clientFactory;
	private final ClientMonitor clientMonitor;
	private final ClientMonitorHandler clientMonitorHandler;
	private final EventLoopGroup eventLoopGroup;

	public AbstractClient(Bootstrap bootstrap, EventLoopGroup eventLoopGroup, boolean unpooledChannels, ChannelPoolHandler poolHandler, SslProvider sslProvider, 
			ClientMonitor clientMonitor, ClientMonitorHandler clientMonitorHandler,
			int poolSize, long poolIdleTimeout, boolean multiplexedRequests) {
		this(clientFactory(multiplexedRequests,bootstrap,eventLoopGroup,poolHandler,sslProvider,clientMonitorHandler,unpooledChannels,poolIdleTimeout,poolSize), clientMonitor, clientMonitorHandler, eventLoopGroup);
	}
	
	public AbstractClient(ClientChannelFactory clientFactory, ClientMonitor clientMonitor, ClientMonitorHandler clientMonitorHandler, EventLoopGroup eventLoopGroup) {
		this.clientFactory=clientFactory;
		this.clientMonitor=clientMonitor;
		this.clientMonitorHandler=clientMonitorHandler;
		this.eventLoopGroup=eventLoopGroup;
	}

	private static ClientChannelFactory clientFactory(boolean multiplexedRequests, Bootstrap bootstrap, EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler, SslProvider sslProvider, ClientMonitorHandler clientMonitorHandler, boolean unpooledChannels, long poolIdleTimeout, int poolSize) {
		if(multiplexedRequests) {
			return multiplexedRequestClientFactory(bootstrap, eventLoopGroup, poolHandler, sslProvider, clientMonitorHandler,unpooledChannels, poolIdleTimeout, poolSize);
		} else {
			return singleRequestClientFactory(bootstrap, eventLoopGroup, poolHandler, sslProvider, clientMonitorHandler, unpooledChannels, poolIdleTimeout, poolSize);
		}
	}

	private static ClientChannelFactory multiplexedRequestClientFactory(Bootstrap bootstrap, EventLoopGroup eventLoopGroup,
			ChannelPoolHandler poolHandler, SslProvider sslProvider, ClientMonitorHandler clientMonitorHandler,
			boolean unpooledChannels, long poolIdleTimeout, int poolSize) {
		if(unpooledChannels) { 
			throw new UnsupportedOperationException();
		} else if(poolSize>0) {
			throw new UnsupportedOperationException();
		} else {
			return new SimpleMultiplexedClientChannelFactory(bootstrap, eventLoopGroup, poolIdleTimeout>0? new IdleTimeoutHandler(poolHandler,poolIdleTimeout):poolHandler, sslProvider,clientMonitorHandler);
		}
	}

	private static ClientChannelFactory singleRequestClientFactory(Bootstrap bootstrap, EventLoopGroup eventLoopGroup,
			ChannelPoolHandler poolHandler, SslProvider sslProvider, ClientMonitorHandler clientMonitorHandler, 
			boolean unpooledChannels, long poolIdleTimeout, int poolSize) {
		if(unpooledChannels) { 
			return new UnpooledClientChannelFactory(bootstrap, eventLoopGroup, poolHandler, sslProvider, clientMonitorHandler);
		} else if(poolSize>0) {
			return new FixedSizeClientChannelFactory(bootstrap, eventLoopGroup, poolIdleTimeout>0? new IdleTimeoutHandler(poolHandler,poolIdleTimeout):poolHandler, sslProvider,clientMonitorHandler, poolSize);
		} else {
			return new SimpleClientChannelFactory(bootstrap, eventLoopGroup, poolIdleTimeout>0? new IdleTimeoutHandler(poolHandler,poolIdleTimeout):poolHandler, sslProvider,clientMonitorHandler);
		}
	}

	@Override
	public ClientMonitor monitor() {
		return clientMonitor;
	}
	
	@Override
	public Future<Void> close() {
		return clientMonitorHandler.idleChannels().close();
	}

	@Override
	public EventLoopGroup eventLoopGroup() {
		return eventLoopGroup;
	}
	
	@Override
	public T request() {
		return request0(clientFactory);
	}

	protected abstract T request0(ClientChannelFactory clientFactory);

}
