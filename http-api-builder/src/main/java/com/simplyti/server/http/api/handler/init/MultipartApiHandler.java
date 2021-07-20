package com.simplyti.server.http.api.handler.init;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.operations.FileUploadApiOperation;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;

public class MultipartApiHandler extends SimpleChannelInboundHandler<HttpRequest> {
	
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;

	private ApiMatchRequest matchRequest;
	
	public MultipartApiHandler(ExceptionHandler exceptionHandler, SyncTaskSubmitter syncTaskSubmitter) {
		super(false);
		this.exceptionHandler=exceptionHandler;
		this.syncTaskSubmitter=syncTaskSubmitter;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		if(matchRequest!=null) {
			serviceProceed(this.matchRequest.operation(),context(ctx,msg));
			this.matchRequest=null;
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private <T extends ApiContext> void serviceProceed(ApiOperation<T> operation, T ctx) {
		try{
			operation.handler().accept(ctx);
		}catch(Throwable e) {
			ctx.failure(e);
		}
	}
	
	private <T extends ApiContext> T context(ChannelHandlerContext ctx, HttpRequest msg) {
		return this.matchRequest.operation().contextFactory().create(syncTaskSubmitter, exceptionHandler, ctx, matchRequest, msg, null);
	}

	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof ApiMatchRequest) {
			if(((ApiMatchRequest) evt).operation() instanceof FileUploadApiOperation) {
				this.matchRequest=(ApiMatchRequest) evt;
			} else {
				ctx.fireUserEventTriggered(evt);
			}
		} else {
			ctx.fireUserEventTriggered(evt);
		}
    }


}
