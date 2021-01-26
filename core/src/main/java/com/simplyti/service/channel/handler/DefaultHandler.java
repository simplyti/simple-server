package com.simplyti.service.channel.handler;

import java.util.Optional;

import javax.inject.Inject;

import com.simplyti.service.exception.NotFoundException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class DefaultHandler extends ChannelInboundHandlerAdapter {
	
	private boolean hasDefaultHandler;

	@Inject
	public DefaultHandler(
			Optional<DefaultBackendFullRequestHandler> defaultBackendFullRequestHandler,
			Optional<DefaultBackendRequestHandler> defaultBackendRequestHandler) {
		this.hasDefaultHandler = defaultBackendFullRequestHandler.isPresent() || defaultBackendRequestHandler.isPresent();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (hasDefaultHandler) {
			ctx.fireChannelRead(msg);
		} else {
			ReferenceCountUtil.release(msg);
			if(msg instanceof HttpRequest) {
				ctx.fireExceptionCaught(new NotFoundException());
			}
		}
	}

}
