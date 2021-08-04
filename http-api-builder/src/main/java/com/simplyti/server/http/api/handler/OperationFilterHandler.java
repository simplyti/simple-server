package com.simplyti.server.http.api.handler;

import java.util.Collection;

import com.simplyti.server.http.api.filter.OperationInboundFilter;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.server.http.api.request.DefaultApiInvocation;
import com.simplyti.service.channel.pending.PendingMessages;
import com.simplyti.service.filter.FilterChain;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;

public class OperationFilterHandler extends ChannelInboundHandlerAdapter {

	private final Collection<OperationInboundFilter> filters;
	
	private ApiMatchRequest matchRequest;
	
	private PendingMessages readPending;
	private boolean discardPerpetually = false;
	private boolean finished = false;

	public OperationFilterHandler(Collection<OperationInboundFilter> filters) {
		this.filters=filters;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(matchRequest !=null) {
			invoke(ctx, msg, matchRequest);
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private void invoke(ChannelHandlerContext ctx, Object msg, ApiMatchRequest matchRequest) {
		if(msg instanceof HttpRequest) {
			DefaultApiInvocation request = DefaultApiInvocation.newInstance((HttpRequest) msg,matchRequest);
			Future<Boolean> futureHandled = FilterChain.of(filters,ctx,request).execute();
			this.readPending=new PendingMessages();
			this.finished = false;
			futureHandled.addListener(f->handleRequestFilter(futureHandled,ctx,(HttpRequest) msg));
		} else if(discardPerpetually) {
			ReferenceCountUtil.release(msg);
			if(msg instanceof LastHttpContent) {
				this.discardPerpetually = false;
			}
		} else {
			this.readPending.pending(msg,ctx.newPromise());
			if(msg instanceof LastHttpContent) {
				this.finished = true;
			}
		}
	}
	
	private void handleRequestFilter(Future<Boolean> future, ChannelHandlerContext ctx, HttpRequest request) {
		if(ctx.executor().inEventLoop()) {
			handleRequestFilter0(future,ctx,request);
		} else {
			ctx.executor().execute(()->handleRequestFilter0(future,ctx,request));
		}
	}

	private void handleRequestFilter0(Future<Boolean> future, ChannelHandlerContext ctx, HttpRequest request) {
		if(future.isSuccess()) {
			if(!future.getNow()) {
				ctx.fireUserEventTriggered(matchRequest);
				ctx.fireChannelRead(request);
				readPending.forEach(msg->ctx.fireChannelRead(msg.msg()));
				readPending=null;
				this.matchRequest=null;
			}else {
				handleDiscard(request);
			}
		} else {
			ctx.fireExceptionCaught(future.cause());
			handleDiscard(request);
		}
	}

	private void handleDiscard(HttpRequest request) {
		ReferenceCountUtil.release(request);
		readPending.successDiscard();
		readPending=null;
		if(finished) {
			this.matchRequest = null;
		} else {
			this.discardPerpetually = true;
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
