package com.simplyti.service.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface DefaultBackendFullRequestHandler {

	void handle(ChannelHandlerContext ctx, FullHttpRequest msg);

}
