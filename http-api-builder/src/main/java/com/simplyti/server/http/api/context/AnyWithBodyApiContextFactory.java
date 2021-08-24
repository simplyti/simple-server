package com.simplyti.server.http.api.context;


import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class AnyWithBodyApiContextFactory implements ApiContextFactory {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ApiContext> T create(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, ApiMatchRequest match, HttpRequest request, ByteBuf body) {
		return (T) new AnyWithBodyApiContextImpl(syncTaskSubmitter, exceptionHandler, ctx,request,body,match);
	}

}
