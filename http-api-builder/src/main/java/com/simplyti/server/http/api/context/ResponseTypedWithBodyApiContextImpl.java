package com.simplyti.server.http.api.context;

import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class ResponseTypedWithBodyApiContextImpl<T> extends AbstractWithBodyApiContext<T> implements ResponseTypedWithBodyApiContext<T> {
	
	private final ByteBuf body;
	
	public ResponseTypedWithBodyApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request, ByteBuf body, ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx, request, match, exceptionHandler, body);
		this.body=body;
	}

	@Override
	public ByteBuf body() {
		return body;
	}

}
