package com.simplyti.server.http.api.context;

import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class ResponseTypedApiContextImpl<T> extends AbstractApiContext<T> implements ResponseTypedApiContext<T> {
	
	public ResponseTypedApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request,ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx, request, match, exceptionHandler);
	}

}
