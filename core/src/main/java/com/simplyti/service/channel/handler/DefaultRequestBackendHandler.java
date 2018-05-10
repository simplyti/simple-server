package com.simplyti.service.channel.handler;

import java.util.Optional;

import javax.inject.Inject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.HttpObject;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultRequestBackendHandler extends SimpleChannelInboundHandler<HttpObject> {
	
	private final Optional<DefaultBackendRequestHandler> defaultBackendHandler;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		defaultBackendHandler.ifPresent(h->h.handle(ctx, msg));
	}

}
