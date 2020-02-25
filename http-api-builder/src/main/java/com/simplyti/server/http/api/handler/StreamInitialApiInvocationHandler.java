package com.simplyti.server.http.api.handler;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;

public class StreamInitialApiInvocationHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private final ApiMatchRequest apiMacher;
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final ExceptionHandler exceptionHandler;

	public StreamInitialApiInvocationHandler(ApiMatchRequest apiMacher,SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler) {
		this.apiMacher=apiMacher;
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.exceptionHandler=exceptionHandler;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		serviceProceed(apiMacher.operation(), context(ctx, msg));
	}
	
	private <T extends ApiContext> void serviceProceed(ApiOperation<T> operation, T ctx) {
		try{
			operation.handler().accept(ctx);
		}catch(Throwable e) {
			throw e;
		}
	}
	
	private <T extends ApiContext> T context(ChannelHandlerContext ctx, HttpRequest msg) {
		return apiMacher.operation().contextFactory().create(syncTaskSubmitter, exceptionHandler, ctx, apiMacher, msg, null);
	}

}
