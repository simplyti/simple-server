package com.simplyti.service.examples.filter;


import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.server.http.api.filter.OperationInboundFilter;
import com.simplyti.server.http.api.handler.ApiInvocation;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.UnauthorizedException;
import com.simplyti.service.filter.FilterContext;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class OperationInboundFilterModule extends AbstractModule implements OperationInboundFilter {
	
	@Inject
	private EventLoopGroup eventLoopGroup;

	@Override
	protected void configure() {
		Multibinder<OperationInboundFilter> fulters = Multibinder.newSetBinder(binder(), OperationInboundFilter.class);
		fulters.addBinding().to(OperationInboundFilterModule.class).in(Singleton.class);
	}

	@Override
	public void execute(FilterContext<ApiInvocation> context) {
		String authorization = context.object().request().headers().get(HttpHeaderNames.AUTHORIZATION);
		if(authorization==null) {
			handle(context.object().match(),()->context.fail(new UnauthorizedException()));
		}else {
			if(context.object().match().parameters().containsKey("handle")) {
				context.done(true);
				handle(context.object().match(),()->context.channel().writeAndFlush(response(context.channel().alloc())));
			} else {
				handle(context.object().match(),()->context.done());
			}
		}
	}

	private FullHttpResponse response(ByteBufAllocator byteBufAllocator) {
		ByteBuf buff = byteBufAllocator.buffer();
		buff.writeCharSequence("Hello filter handle!", CharsetUtil.UTF_8);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, buff);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH,buff.readableBytes());
		return response;
	}

	private void handle(ApiMatchRequest match, Runnable call) {
		if(match.parameters().get("delay") != null) {
			int delay = Integer.parseInt(match.parameters().get("delay").get(0));
			eventLoopGroup.next().schedule(call, delay, TimeUnit.MILLISECONDS);
		} else {
			call.run();
		}
		
	}

}