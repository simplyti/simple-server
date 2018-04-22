package com.simplyti.service.channel.handler;


import io.netty.channel.ChannelHandler.Sharable;

import java.util.Set;

import com.google.inject.Inject;
import com.simplyti.service.api.ApiInvocation;
import com.simplyti.service.api.DefaultApiInvocationContext;
import com.simplyti.service.api.filter.FilterChain;
import com.simplyti.service.api.filter.OperationInboundFilter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ApiInvocationHandler extends SimpleChannelInboundHandler<ApiInvocation<?>> {
	
	private final Set<OperationInboundFilter> filters;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ApiInvocation<?> msg) throws Exception {
		invoke(ctx,msg);
	}

	@SuppressWarnings({ "rawtypes" })
	private <I,O> void invoke(ChannelHandlerContext ctx, ApiInvocation msg) {
		if(filters.isEmpty()) {
			serviceProceed(ctx,msg);
		}else {
			FilterChain.of(filters,ctx,msg).execute()
				.addListener(result->{
					if(result.isSuccess()) {
						serviceProceed(ctx,msg);
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
		return new DefaultApiInvocationContext<I,O>(ctx,msg);
	}

}
