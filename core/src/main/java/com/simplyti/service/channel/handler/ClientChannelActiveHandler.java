package com.simplyti.service.channel.handler;

import javax.inject.Inject;

import com.simplyti.service.Service;
import com.simplyti.service.channel.ClientChannelGroup;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ClientChannelActiveHandler extends ChannelDuplexHandler {
	
private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final Service service;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).set(true);
		ctx.fireChannelRead(msg);
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		promise.addListener(future -> {
			ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).set(false);
			if(service.stopping()){
				log.info("Server is stopping, close channel");
				ctx.channel().close();
			}
		});
		ctx.write(msg, promise);
	}

}
