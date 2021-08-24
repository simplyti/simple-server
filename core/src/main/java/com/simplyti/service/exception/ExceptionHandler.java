package com.simplyti.service.exception;

import com.simplyti.util.concurrent.Future;

import io.netty.channel.ChannelHandlerContext;

public interface ExceptionHandler {
	
	Future<Void> exceptionCaught(ChannelHandlerContext ctx, Throwable cause);

}
