package com.simplyti.service.clients.channel.proxy;

import com.simplyti.service.clients.channel.ClientChannelEvent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.proxy.ProxyConnectionEvent;

@Sharable
public class ProxyConnectHandler extends ChannelInboundHandlerAdapter {
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt != ClientChannelEvent.INIT) {
        	ctx.fireUserEventTriggered(evt);
        } 
        if(evt instanceof ProxyConnectionEvent) {
        	ctx.pipeline().remove(this);
        	ctx.fireUserEventTriggered(ClientChannelEvent.INIT);
        }
    }
	
}
