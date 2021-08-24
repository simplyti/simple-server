package com.simplyti.service.clients.channel.handler;

import com.simplyti.service.commons.netty.Promises;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class MultiplexChannelToParentHandler extends ChannelOutboundHandlerAdapter {

	private final Channel channel;

	public MultiplexChannelToParentHandler(Channel channel) {
		this.channel=channel;
	}

	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		Promises.toPromise(channel.write(msg), promise);
	}
	
	@Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
		channel.flush();
    }

}
