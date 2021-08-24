package com.simplyti.service.channel.handler;

import java.util.List;

import javax.inject.Inject;

import com.simplyti.service.filter.FilterChain;
import com.simplyti.service.filter.http.FullHttpRequestFilter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;

public class FullHttpRequestFilterHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private final List<FullHttpRequestFilter> filters;
	
	@Inject
	public FullHttpRequestFilterHandler(List<FullHttpRequestFilter> filters) {
		super(false);
		this.filters=filters;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		Future<Boolean> futureHandled = FilterChain.of(filters, ctx, msg).execute();
		futureHandled.addListener(f->handleRequestFilter(futureHandled,ctx,(HttpRequest) msg));
	}
	
	private void handleRequestFilter(Future<Boolean> future, ChannelHandlerContext ctx, HttpRequest request) {
		if(future.isSuccess()) {
			if(!future.getNow()) {
				ctx.fireChannelRead(request);
			}else {
				ReferenceCountUtil.release(request);
			}
		}else {
			ReferenceCountUtil.release(request);
			ctx.fireExceptionCaught(future.cause());
		}
	}

}
