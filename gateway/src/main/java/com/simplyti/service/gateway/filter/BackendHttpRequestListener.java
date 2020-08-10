package com.simplyti.service.gateway.filter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface BackendHttpRequestListener  {

	void startRequest(ChannelHandlerContext ctx, HttpRequest request);
	
	void endRequest(ChannelHandlerContext ctx);

	void startResponse(ChannelHandlerContext ctx, HttpResponse response);
	
	void endResponse(ChannelHandlerContext ctx);

	void acquired(ChannelHandlerContext ctx, Channel channel);

}
