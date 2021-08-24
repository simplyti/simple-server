package com.simplyti.service.clients.channel.handler;

import java.nio.channels.ClosedChannelException;

import com.simplyti.service.clients.channel.ClientChannelEvent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class PrematureErrorHandler extends ChannelInboundHandlerAdapter {
	
	private Throwable prematureException;

	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt == ClientChannelEvent.INIT) {
        	ctx.pipeline().remove(this);
        	if(prematureException != null) {
        		ctx.pipeline().fireExceptionCaught(prematureException);
        	}
        } 
        ctx.fireUserEventTriggered(evt);
    }
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		this.prematureException = cause;
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.prematureException = new ClosedChannelException();
    }

}
