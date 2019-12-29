package com.simplyti.service.clients.http.sse.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.CharsetUtil;

public class EventFrameDecoder extends DelimiterBasedFrameDecoder {

	private static final ByteBuf DELIMITER = Unpooled.copiedBuffer("\n\n", CharsetUtil.UTF_8);

	public EventFrameDecoder() {
		super(100000,DELIMITER);
	}

	public ByteBuf decode(ChannelHandlerContext ctx, HttpContent content) throws Exception {
		return (ByteBuf) decode(ctx, content.content());
	}

}
