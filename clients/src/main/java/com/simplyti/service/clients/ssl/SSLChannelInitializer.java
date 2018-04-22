package com.simplyti.service.clients.ssl;

import static io.vavr.control.Try.of;

import com.simplyti.service.clients.Address;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class SSLChannelInitializer extends AbstractChannelPoolHandler{
	
	private final SslContext sslCtx;
	private final ChannelPoolHandler nestedInitializer;
	private final Address address;

	public SSLChannelInitializer(ChannelPoolHandler nestedInitializer, Address address) {
		this.sslCtx = of(()->SslContextBuilder
				.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build()).get();
		this.nestedInitializer=nestedInitializer;
		this.address=address;
	}
	
	@Override
	public void channelCreated(Channel ch) throws Exception {
		ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(),address.host(),address.port()));
		nestedInitializer.channelCreated(ch);
	}
	
}
