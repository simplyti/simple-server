package com.simplyti.service;

import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class NotFoundFullHandler extends DefaultBackendFullRequestHandler {
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		ByteBuf body = Unpooled.copiedBuffer("This is a custom handler", CharsetUtil.UTF_8);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND,body);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH,body.readableBytes());
		ctx.writeAndFlush(response);
	}

}
