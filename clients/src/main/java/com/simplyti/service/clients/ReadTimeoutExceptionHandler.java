package com.simplyti.service.clients;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.ReadTimeoutException;

@Sharable
public class ReadTimeoutExceptionHandler extends ChannelInboundHandlerAdapter {


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(cause instanceof ReadTimeoutException) {
			ctx.close();
		} else {
			ctx.fireExceptionCaught(cause);
		}
	}

}
