package com.simplyti.service.clients.ssl;

import javax.net.ssl.SSLEngine;

import com.simplyti.service.clients.Endpoint;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.SneakyThrows;

public class SSLChannelInitializer extends AbstractChannelPoolHandler{
	
	private final SslContext sslCtx;
	private final ChannelPoolHandler nestedInitializer;
	private final Endpoint endpoint;
	private final boolean requireClientAuth;

	@SneakyThrows
	public SSLChannelInitializer(SslProvider sslProvider, ChannelPoolHandler nestedInitializer, Endpoint endpoint) {
		SslContextBuilder builder = SslContextBuilder
				.forClient();
		if(endpoint instanceof SSLEndpoint) {
			this.requireClientAuth=true;
			SSLEndpoint sslEndpoint = (SSLEndpoint) endpoint;
			builder.keyManager(sslEndpoint.key(),sslEndpoint.certs());
		} else {
			this.requireClientAuth = false;
		}
		this.sslCtx = builder
				.sslProvider(sslProvider)
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();
		this.nestedInitializer=nestedInitializer;
		this.endpoint=endpoint;
	}
	
	@Override
	public void channelCreated(Channel ch) throws Exception {
		SSLEngine engine = sslCtx.newEngine(ch.alloc(),endpoint.address().host(),endpoint.address().port());
		engine.setNeedClientAuth(requireClientAuth);
		SslHandler context = new SslHandler(engine);
		ch.pipeline().addLast(context);
		nestedInitializer.channelCreated(ch);
	}
	
}
