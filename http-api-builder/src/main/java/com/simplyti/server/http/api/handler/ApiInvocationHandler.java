package com.simplyti.server.http.api.handler;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.WithBodyApiContext;
import com.simplyti.server.http.api.filter.OperationInboundFilter;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.server.http.api.request.FullApiInvocation;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.filter.FilterChain;
import com.simplyti.service.filter.priority.Priorized;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.Future;

public class ApiInvocationHandler extends ChannelInboundHandlerAdapter {
	
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final Collection<OperationInboundFilter> filters;
	private final ExceptionHandler exceptionHandler;
	
	private ApiMatchRequest matchRequest;
	private ApiContext context;
	
	@Inject
	public ApiInvocationHandler(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, Set<OperationInboundFilter> filters) {
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.exceptionHandler=exceptionHandler;
		this.filters=filters.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
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
		if(filters.isEmpty()) {
			serviceProceed(matchRequest.operation(),context(ctx, msg, matchRequest));
		}else {
			FullApiInvocation request = FullApiInvocation.newInstance(msg,matchRequest);
			Future<Boolean> futureHandled = FilterChain.of(filters,ctx,request).execute();
			futureHandled.addListener(result->{
					request.recycle();
					if(result.isSuccess()) {
						if(!futureHandled.getNow()) {
							serviceProceed(matchRequest.operation(),context(ctx, msg, matchRequest));
						} else {
							msg.release();
						}
					}else {
						msg.release();
						ctx.fireExceptionCaught(result.cause());
					}
				});
		}
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
