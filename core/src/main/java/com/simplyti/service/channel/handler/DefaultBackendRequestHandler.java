package com.simplyti.service.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;

public interface DefaultBackendRequestHandler {

	void handle(ChannelHandlerContext ctx, HttpObject msg);
	
}
