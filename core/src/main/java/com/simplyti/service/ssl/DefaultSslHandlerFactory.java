package com.simplyti.service.ssl;

import javax.inject.Inject;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultSslHandlerFactory implements SslHandlerFactory{
	
	private final SslContext sslCtx;

	@Override
	public SslHandler handler(Channel channel) {
		return new SslHandler(sslCtx.newEngine(channel.alloc()));
	}

}
