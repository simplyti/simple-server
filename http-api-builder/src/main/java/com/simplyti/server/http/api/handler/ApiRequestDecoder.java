package com.simplyti.server.http.api.handler;

import javax.inject.Inject;

import com.simplyti.server.http.api.operations.ApiOperationResolver;
import com.simplyti.server.http.api.request.ApiMatchRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.HttpRequest;

@Sharable
public class ApiRequestDecoder extends ChannelInboundHandlerAdapter {
	
	private final ApiOperationResolver apiResolver;
	
	@Inject
	public ApiRequestDecoder(ApiOperationResolver apiResolver) {
		this.apiResolver=apiResolver;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpRequest) {
			HttpRequest httpMsg = (HttpRequest) msg;
			ApiMatchRequest matchRequest = apiResolver.resolveOperation(httpMsg.method(),httpMsg.uri());
			if(matchRequest != null) {
				ctx.fireUserEventTriggered(matchRequest);
			} 
		}
		ctx.fireChannelRead(msg);
	}


}
