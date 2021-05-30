package com.simplyti.service.clients;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.monitor.DefaultClientMonitor;
import com.simplyti.service.clients.request.DefaultGenericRequestBuilder;
import com.simplyti.service.clients.request.GenericRequestBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;

public class DefaultGenericClient extends AbstractClient<GenericRequestBuilder> implements GenericClient {

	private final Endpoint endpoint;

	public DefaultGenericClient(EventLoopGroup eventLoopGroup, Bootstrap bootstrap, Endpoint endpoint, SslProvider sslProvider, DefaultClientMonitor monitor,
			int poolSize, boolean unpooledChannels, long poolIdleTimeout, long readTimeoutMillis, boolean verbose, Consumer<Channel> customInit) {
		super(bootstrap,eventLoopGroup,unpooledChannels, new GenericClientChannelPoolHandler(readTimeoutMillis, verbose, customInit), sslProvider, monitor , monitor, poolSize, poolIdleTimeout, false);
		this.endpoint=endpoint;
	}

	@Override
	protected GenericRequestBuilder request0(ClientChannelFactory clientFactory) {
		return new DefaultGenericRequestBuilder(clientFactory,endpoint);
	}

}
