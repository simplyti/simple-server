package com.simplyti.service.clients.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;

public class ErrorHandler extends ChannelInboundHandlerAdapter {

	private final Promise<?> promise;

	public ErrorHandler(Promise<?> promise) {
		this.promise = promise;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		promise.tryFailure(cause);
		ctx.close();
	}

}