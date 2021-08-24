package com.simplyti.service.clients.proxy.channel;

import java.net.SocketAddress;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class NoResolvingSocketAddressUnwarpHandler extends ChannelDuplexHandler {
	
	 @Override
	    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
	                        SocketAddress localAddress, ChannelPromise promise) throws Exception {
	        ctx.connect(((NoResolvingSocketAddress)remoteAddress).unwrap(), localAddress, promise);
	    }

}
