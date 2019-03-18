package com.simplyti.service.channel.handler;


import io.netty.channel.ChannelHandler.Sharable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.service.api.ApiInvocation;
import com.simplyti.service.api.DefaultApiInvocationContext;
import com.simplyti.service.api.filter.FilterChain;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sse.ServerSentEventEncoder;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;

@Sharable
public class ApiInvocationHandler extends SimpleChannelInboundHandler<ApiInvocation<?>> {
	
	private static final Comparator<Object> PRIORITY_ANN_ORDER = (o1,o2)-> {
		Integer o1Priority = Optional.ofNullable(o1.getClass().getAnnotation(Priority.class))
			.map(priority->priority.value())
			.orElse(Integer.MAX_VALUE);
		
		Integer o2Priority = Optional.ofNullable(o2.getClass().getAnnotation(Priority.class))
				.map(priority->priority.value())
				.orElse(Integer.MAX_VALUE);
		
		return o1Priority.compareTo(o2Priority);
	};
	
	private final List<OperationInboundFilter> filters;
	private final ExceptionHandler exceptionHandler;
	private final ServerSentEventEncoder serverEventEncoder;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	@Inject
	public ApiInvocationHandler(Set<OperationInboundFilter> filters, ExceptionHandler exceptionHandler, ServerSentEventEncoder serverEventEncoder,
			SyncTaskSubmitter syncTaskSubmitter) {
		this.filters=filters.stream().sorted(PRIORITY_ANN_ORDER).collect(Collectors.toList());
		this.exceptionHandler=exceptionHandler;
		this.serverEventEncoder=serverEventEncoder;
		this.syncTaskSubmitter=syncTaskSubmitter;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ApiInvocation<?> msg) throws Exception {
		invoke(ctx,msg);
	}

	@SuppressWarnings({ "rawtypes" })
	private <I,O> void invoke(ChannelHandlerContext ctx, ApiInvocation msg) {
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
	private <I,O> void serviceProceed(ChannelHandlerContext ctx,ApiInvocation msg) {
		DefaultApiInvocationContext<I, O> context = context(ctx,msg);
		try{
			msg.operation().handler().accept(ReferenceCountUtil.retain(context));
		}catch(Throwable e) {
			context.tryRelease();
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <I,O> DefaultApiInvocationContext<I,O> context(ChannelHandlerContext ctx, ApiInvocation msg) {
		return new DefaultApiInvocationContext<I,O>(ctx,msg,exceptionHandler,serverEventEncoder,syncTaskSubmitter);
	}

}
