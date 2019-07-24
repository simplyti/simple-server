package com.simplyti.service.channel.handler;


import io.netty.channel.ChannelHandler.Sharable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.simplyti.service.api.FullApiInvocation;
import com.simplyti.service.api.DefaultApiInvocationContext;
import com.simplyti.service.api.filter.FilterChain;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.priority.Priorized;
import com.simplyti.service.sse.ServerSentEventEncoder;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;

@Sharable
public class ApiInvocationHandler extends SimpleChannelInboundHandler<FullApiInvocation<?>> {
	
	private final List<OperationInboundFilter> filters;
	private final ExceptionHandler exceptionHandler;
	private final ServerSentEventEncoder serverEventEncoder;
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final Json json;
	
	@Inject
	public ApiInvocationHandler(Set<OperationInboundFilter> filters, ExceptionHandler exceptionHandler, ServerSentEventEncoder serverEventEncoder,
			SyncTaskSubmitter syncTaskSubmitter, Json json) {
		this.filters=filters.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
		this.exceptionHandler=exceptionHandler;
		this.serverEventEncoder=serverEventEncoder;
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.json=json;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullApiInvocation<?> msg) throws Exception {
		invoke(ctx,msg);
	}

	@SuppressWarnings({ "rawtypes" })
	private <I,O> void invoke(ChannelHandlerContext ctx, FullApiInvocation msg) {
		if(filters.isEmpty()) {
			serviceProceed(ctx,msg);
		}else {
			Future<Boolean> futureHandled = FilterChain.of(filters,ctx,msg).execute();
			futureHandled.addListener(result->{
					if(result.isSuccess()) {
						if(!futureHandled.getNow()) {
							serviceProceed(ctx,msg);
						}
					}else {
						ctx.fireExceptionCaught(result.cause());
					}
				});
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <I,O> void serviceProceed(ChannelHandlerContext ctx,FullApiInvocation msg) {
		DefaultApiInvocationContext<I, O> context = context(ctx,msg);
		try{
			msg.operation().handler().accept(ReferenceCountUtil.retain(context));
		}catch(Throwable e) {
			context.tryRelease();
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <I,O> DefaultApiInvocationContext<I,O> context(ChannelHandlerContext ctx, FullApiInvocation msg) {
		return new DefaultApiInvocationContext<I,O>(ctx,msg.matcher(),msg,exceptionHandler,serverEventEncoder,syncTaskSubmitter,json);
	}

}
