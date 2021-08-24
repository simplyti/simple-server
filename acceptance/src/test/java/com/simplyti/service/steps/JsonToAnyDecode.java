package com.simplyti.service.steps;

import com.jsoniter.JsonIterator;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class JsonToAnyDecode extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof ByteBuf) {
			ByteBuf buff = (ByteBuf) msg;
			byte[] data = new byte[buff.readableBytes()];
			buff.readBytes(data);
			buff.release();
			ctx.fireChannelRead(JsonIterator.deserialize(data));
		} else {
			ReferenceCountUtil.release(msg);
		}
	}

}
