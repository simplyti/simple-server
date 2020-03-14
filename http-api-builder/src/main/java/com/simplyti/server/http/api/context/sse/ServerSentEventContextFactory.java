package com.simplyti.server.http.api.context.sse;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.server.http.api.sse.ServerSentEventEncoder;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class ServerSentEventContextFactory implements ApiContextFactory {
	
	private final ServerSentEventEncoder serverEventEncoder;
	
	public ServerSentEventContextFactory() {
		this.serverEventEncoder = new ServerSentEventEncoder();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ApiContext> T create(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler,
			ChannelHandlerContext ctx, ApiMatchRequest match, HttpRequest request, ByteBuf body) {
		return (T) new ServerSentEventApiContextImpl(syncTaskSubmitter, ctx, request, match, serverEventEncoder);
	}

}
