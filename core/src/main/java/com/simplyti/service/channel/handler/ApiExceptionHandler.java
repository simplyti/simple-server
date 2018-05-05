package com.simplyti.service.channel.handler;

import javax.inject.Inject;

import com.simplyti.service.exception.ExceptionHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import lombok.AllArgsConstructor;

@Sharable
@AllArgsConstructor(onConstructor = @__(@Inject))
public class ApiExceptionHandler extends ChannelInboundHandlerAdapter {
	
	private final ExceptionHandler exceptionHandler;
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		exceptionHandler.exceptionCaught(ctx,cause);
	}

}
