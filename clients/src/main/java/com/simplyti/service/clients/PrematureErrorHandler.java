package com.simplyti.service.clients;

import com.simplyti.service.clients.channel.ClientChannelEvent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class PrematureErrorHandler extends ChannelInboundHandlerAdapter {
	
	private Throwable prematureException;

	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt == ClientChannelEvent.INIT) {
        	if(prematureException != null) {
        		ctx.fireExceptionCaught(prematureException);
        	}
        	ctx.pipeline().remove(this);
        } 
        ctx.fireUserEventTriggered(evt);
    }
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		this.prematureException = cause;
    }

}
