package com.simplyti.service.clients;

import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.channel.FixedSizeClientChannelFactory;
import com.simplyti.service.clients.channel.SimpleClientChannelFactory;
import com.simplyti.service.clients.channel.UnpooledClientChannelFactory;
import com.simplyti.service.clients.channel.handler.IdleTimeoutHandler;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.monitor.ClientMonitor;
import com.simplyti.service.clients.monitor.DefaultClientMonitor;
import com.simplyti.service.clients.request.DefaultGenericRequestBuilder;
import com.simplyti.service.clients.request.GenericRequestBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.concurrent.Future;

public class DefaultGenericClient extends AbstractChannelPoolHandler implements GenericClient {

	private final ClientChannelFactory clientFactory;
	private final Endpoint endpoint;
	private final DefaultClientMonitor monitor;

	public DefaultGenericClient(EventLoopGroup eventLoopGroup, Bootstrap bootstrap, Endpoint endpoint, SslProvider sslProvider, DefaultClientMonitor monitor,
			int poolSize, boolean unpooledChannels, long poolIdleTimeout) {
		if(unpooledChannels) {
			this.clientFactory=new UnpooledClientChannelFactory(bootstrap, eventLoopGroup, this, sslProvider, monitor);
		} else if(poolSize>0) {
			this.clientFactory=new FixedSizeClientChannelFactory(bootstrap, eventLoopGroup, poolIdleTimeout>0? new IdleTimeoutHandler(this,poolIdleTimeout):this, sslProvider, monitor, poolSize);
		} else {
			this.clientFactory=new SimpleClientChannelFactory(bootstrap, eventLoopGroup, poolIdleTimeout>0? new IdleTimeoutHandler(this,poolIdleTimeout):this, sslProvider, monitor);
		}
		this.monitor=monitor;
		this.endpoint=endpoint;
	}

	@Override
	public GenericRequestBuilder request() {
		return new DefaultGenericRequestBuilder(clientFactory,endpoint);
	}

	@Override
	public void channelCreated(Channel ch) throws Exception {}

	@Override
	public ClientMonitor monitor() {
		return monitor;
	}

	@Override
	public Future<Void> close() {
		return monitor.idleChannels().close();
	}

}
