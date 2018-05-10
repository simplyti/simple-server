package com.simplyti.service.channel.handler;

import io.netty.channel.ChannelHandler.Sharable;

import java.util.Optional;

import javax.inject.Inject;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultFullRequestBackendHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	private final Optional<DefaultBackendFullRequestHandler> defaultBackendHandler;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		defaultBackendHandler.ifPresent(h->h.handle(ctx, msg));
	}

}
