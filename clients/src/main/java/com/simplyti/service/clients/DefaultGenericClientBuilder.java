package com.simplyti.service.clients;

import java.util.function.Consumer;

import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.monitor.DefaultClientMonitor;
import com.simplyti.service.clients.request.GenericRequestBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;

public class DefaultGenericClientBuilder extends AbstractClientBuilder<GenericClientBuilder, GenericClient, GenericRequestBuilder>  implements GenericClientBuilder {

	private Consumer<Channel> init;

	@Override
	protected GenericClient build0(EventLoopGroup eventLoopGroup, Bootstrap bootstrap, Endpoint endpoint, SslProvider sslProvider, DefaultClientMonitor monitor, int poolSize, boolean unpooledChannels,
			long poolIdleTimeout, long readTimeoutMilis,
			boolean verbose) {
		return new DefaultGenericClient(eventLoopGroup, bootstrap, endpoint, sslProvider, monitor, poolSize, unpooledChannels, poolIdleTimeout, readTimeoutMilis, verbose, init);
	}

	@Override
	public GenericClientBuilder withInitializer(Consumer<Channel> init) {
		this.init=init;
		return this;
	}

}
