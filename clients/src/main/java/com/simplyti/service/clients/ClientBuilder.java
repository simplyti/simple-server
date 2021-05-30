package com.simplyti.service.clients;

import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.request.BaseClientRequestBuilder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;

public interface ClientBuilder<B,T extends Client<R>,R extends BaseClientRequestBuilder<R>> {

	B withEventLoopGroup(EventLoopGroup eventLoopGroup);
	B eventLoopGroup(EventLoopGroup eventLoopGroup);
	
	B withEventLoopGroupFactory(EventLoopGroupFactory factory);
	
	B withChannelFactory(ChannelFactory<Channel> factory);
	
	B withEndpoint(String host, int port);
	
	B withEndpoint(Endpoint endpoint);
	
	B withSslProvider(SslProvider sslProvider);
	
	B withMonitorEnabled();
	
	B withMonitorEnabled(boolean enabled);
	
	B withChannelPoolSize(int poolSize);
	
	B withUnpooledChannels();
	
	B withChannelPoolIdleTimeout(long poolIdleTimeout);
	
	B withReadTimeout(long timeoutMillis);
	
	B verbose();

	T build();

}
