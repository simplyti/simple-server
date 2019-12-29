package com.simplyti.service.clients;

import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.monitor.DefaultClientMonitor;
import com.simplyti.service.clients.request.GenericRequestBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;

public class DefaultGenericClientBuilder extends AbstractClientBuilder<GenericClientBuilder, GenericClient, GenericRequestBuilder>  implements GenericClientBuilder {

	@Override
	protected GenericClient build0(EventLoopGroup eventLoopGroup, Bootstrap bootstrap, Endpoint endpoint, SslProvider sslProvider, DefaultClientMonitor monitor, int poolSize, boolean unpooledChannels,
			long poolIdleTimeout) {
		return new DefaultGenericClient(eventLoopGroup,bootstrap,endpoint,sslProvider, monitor, poolSize, unpooledChannels, poolIdleTimeout);
	}

}
