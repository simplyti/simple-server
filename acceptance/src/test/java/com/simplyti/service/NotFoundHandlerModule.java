package com.simplyti.service;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class NotFoundHandlerModule extends AbstractModule implements DefaultBackendRequestHandler{

	@Override
	protected void configure() {
		bind(DefaultBackendRequestHandler.class).to(NotFoundHandlerModule.class).in(Singleton.class);
	}

	@Override
	public void handle(ChannelHandlerContext ctx, HttpObject msg) {
		if(msg instanceof LastHttpContent) {
			ByteBuf body = Unpooled.copiedBuffer("This is a custom handler", CharsetUtil.UTF_8);
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND,body);
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH,body.readableBytes());
			ctx.writeAndFlush(response);
		}
	}

}
