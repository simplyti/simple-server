package com.simplyti.service.clients.ssl;

import com.simplyti.service.clients.Endpoint;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.SneakyThrows;

public class SSLChannelInitializer extends AbstractChannelPoolHandler{
	
	private final SslContext sslCtx;
	private final ChannelPoolHandler nestedInitializer;
	private final Endpoint endpoint;

	@SneakyThrows
	public SSLChannelInitializer(ChannelPoolHandler nestedInitializer, Endpoint endpoint) {
		SslContextBuilder builder = SslContextBuilder
				.forClient();
		if(endpoint instanceof SSLEndpoint) {
			SSLEndpoint sslEndpoint = (SSLEndpoint) endpoint;
			builder.keyManager(sslEndpoint.key(),sslEndpoint.certs());
		}
		this.sslCtx = builder.trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		this.nestedInitializer=nestedInitializer;
		this.endpoint=endpoint;
	}
	
	@Override
	public void channelCreated(Channel ch) throws Exception {
		ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(),endpoint.address().host(),endpoint.address().port()));
		nestedInitializer.channelCreated(ch);
	}
	
}
