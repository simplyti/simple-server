package com.simplyti.service.gateway.filter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface BackendHttpRequestListener  {

	void sentRequest(ChannelHandlerContext ctx, HttpRequest request);

	void receivedResponse(ChannelHandlerContext ctx, HttpResponse response);

	void acquired(ChannelHandlerContext ctx, Channel channel);

}
