package com.simplyti.server.http.api.handler;

import javax.inject.Inject;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.WithBodyApiContext;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

public class ApiInvocationHandler extends ChannelInboundHandlerAdapter {
	
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final ExceptionHandler exceptionHandler;
	
	private ApiMatchRequest matchRequest;
	private ApiContext context;
	
	@Inject
	public ApiInvocationHandler(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler) {
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.exceptionHandler=exceptionHandler;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(matchRequest !=null && msg instanceof FullHttpRequest) {
			invoke(ctx, (FullHttpRequest) msg, matchRequest);
			this.matchRequest=null;
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	private <I,O> void invoke(ChannelHandlerContext ctx, FullHttpRequest msg, ApiMatchRequest matchRequest) {
		serviceProceed(matchRequest.operation(),context(ctx, msg, matchRequest));
	}

	private <T extends ApiContext> void serviceProceed(ApiOperation<T> operation, T ctx) {
		try{
			operation.handler().accept(ctx);
		}catch(Throwable e) {
			ctx.failure(e);
		}
	}

	private <T extends ApiContext> T context(ChannelHandlerContext ctx, FullHttpRequest msg, ApiMatchRequest matchRequest) {
		T context = matchRequest.operation().contextFactory().create(syncTaskSubmitter, exceptionHandler, ctx, matchRequest, msg, msg.content());
		this.context = context;
		return context;
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(this.context instanceof WithBodyApiContext) {
        	((WithBodyApiContext)this.context).release();
        }
    }
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof ApiMatchRequest) {
			this.matchRequest=(ApiMatchRequest) evt;
		} else {
			ctx.fireUserEventTriggered(evt);
		}
    }

}
