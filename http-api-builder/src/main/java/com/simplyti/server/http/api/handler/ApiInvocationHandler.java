package com.simplyti.server.http.api.handler;


import io.netty.channel.ChannelHandler.Sharable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.WithBodyApiContext;
import com.simplyti.server.http.api.filter.OperationInboundFilter;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.request.FullApiInvocation;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.filter.FilterChain;
import com.simplyti.service.priority.Priorized;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;

@Sharable
public class ApiInvocationHandler extends SimpleChannelInboundHandler<FullApiInvocation> {
	
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final Collection<OperationInboundFilter> filters;
	private final ExceptionHandler exceptionHandler;
	
	private ApiContext context;
	
	@Inject
	public ApiInvocationHandler(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, Set<OperationInboundFilter> filters) {
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.exceptionHandler=exceptionHandler;
		this.filters=filters.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullApiInvocation msg) throws Exception {
		invoke(ctx,msg);
	}

	private <I,O> void invoke(ChannelHandlerContext ctx, FullApiInvocation msg) {
		if(filters.isEmpty()) {
			serviceProceed(msg.match().operation(),context(ctx, msg));
		}else {
			Future<Boolean> futureHandled = FilterChain.of(filters,ctx,msg).execute();
			futureHandled.addListener(result->{
					if(result.isSuccess()) {
						if(!futureHandled.getNow()) {
							serviceProceed(msg.match().operation(),context(ctx, msg));
						} else {
							msg.request().release();
						}
					}else {
						msg.request().release();
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

	private <T extends ApiContext> T context(ChannelHandlerContext ctx, FullApiInvocation msg) {
		T context = msg.match().operation().contextFactory().create(syncTaskSubmitter, exceptionHandler, ctx, msg.match(), msg.request(), msg.request().content());
		this.context = context;
		return context;
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(this.context instanceof WithBodyApiContext) {
        	((WithBodyApiContext)this.context).release();
        }
    }

}
