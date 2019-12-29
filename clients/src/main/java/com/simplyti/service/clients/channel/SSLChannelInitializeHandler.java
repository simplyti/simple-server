package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.endpoint.ssl.SSLEndpoint;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.SneakyThrows;

public class SSLChannelInitializeHandler extends AbstractChannelPoolHandler {
	
	private final SslContext sslCtx;
	private final ChannelPoolHandler nestedInitializer;
	private final Endpoint endpoint;

	@SneakyThrows
	public SSLChannelInitializeHandler(SslProvider sslProvider, ChannelPoolHandler nestedInitializer, Endpoint endpoint) {
		SslContextBuilder builder = SslContextBuilder
				.forClient();
		if(endpoint instanceof SSLEndpoint) {
			SSLEndpoint sslEndpoint = (SSLEndpoint) endpoint;
			builder.keyManager(sslEndpoint.key(),sslEndpoint.certs());
		}
		this.sslCtx = builder
				.sslProvider(sslProvider)
				.trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		this.nestedInitializer=nestedInitializer;
		this.endpoint=endpoint;
	}
	
	@Override
	public void channelCreated(Channel ch) throws Exception {
		ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(),endpoint.address().host(),endpoint.address().port()));
		nestedInitializer.channelCreated(ch);
	}
	
}
