package com.simplyti.service;

import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class NotFoundHandler extends SimpleChannelInboundHandler<Object> implements DefaultBackendRequestHandler {
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		ReferenceCountUtil.release(msg);
		if(msg instanceof LastHttpContent) {
			ByteBuf body = Unpooled.copiedBuffer("This is a custom handler", CharsetUtil.UTF_8);
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND,body);
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH,body.readableBytes());
			ctx.writeAndFlush(response);
		}
	}

}
