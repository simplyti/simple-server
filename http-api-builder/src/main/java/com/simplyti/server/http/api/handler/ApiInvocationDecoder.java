package com.simplyti.server.http.api.handler;

import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.server.http.api.request.FullApiInvocation;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class ApiInvocationDecoder extends SimpleChannelInboundHandler<FullHttpRequest>{
	
	private final ApiMatchRequest apiMacher;
	
	public ApiInvocationDecoder(ApiMatchRequest apiMacher) {
		super(false);
		this.apiMacher=apiMacher;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		ctx.fireChannelRead(new FullApiInvocation(apiMacher,msg));
	}

}
