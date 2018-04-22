package com.simplyti.service.client;

import static io.vavr.control.Try.of;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class SSLClientChannelInitializer extends AbstractChannelPoolHandler{
	
	private final SslContext sslCtx;
	private final ChannelPoolHandler nestedInitializer;
	private final String sni;
	private final int port;

	public SSLClientChannelInitializer(String sni,int port,ChannelPoolHandler nestedInitializer) {
		this.sslCtx = of(()->SslContextBuilder
				.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build()).get();
		this.sni=sni;
		this.port=port;
		this.nestedInitializer=nestedInitializer;
	}
	
	@Override
	public void channelCreated(Channel ch) throws Exception {
		SslHandler handler = sslCtx.newHandler(ch.alloc(),sni,port);
		ch.pipeline().addLast(handler);
		nestedInitializer.channelCreated(ch);
	}
	
}
