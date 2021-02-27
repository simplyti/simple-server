package com.simplyti.service.channel.handler;

import java.util.List;

import com.simplyti.service.channel.pending.PendingMessages;
import com.simplyti.service.filter.FilterChain;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;

public class HttpRequestFilterHandler extends ChannelInboundHandlerAdapter {

	private final List<HttpRequestFilter> filters;
	
	private PendingMessages readPending;
	private boolean discardPerpetually = false;

	public HttpRequestFilterHandler(List<HttpRequestFilter> filters) {
		this.filters=filters;
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(discardPerpetually) {
			ReferenceCountUtil.release(msg);
			if(msg instanceof LastHttpContent) {
				this.discardPerpetually = false;
			}
		} else if(msg instanceof HttpRequest) {
			Future<Boolean> futureHandled = FilterChain.of(filters, ctx, (HttpRequest) msg).execute();
			this.readPending=new PendingMessages();
			futureHandled.addListener(f->handleRequestFilter(futureHandled,ctx,(HttpRequest) msg));
		} else if(readPending!=null) {
			this.readPending.pending(msg,ctx.newPromise());
		} else {
			ctx.fireChannelRead(msg);
		}
    }
	
	private void handleRequestFilter(Future<Boolean> future, ChannelHandlerContext ctx, HttpRequest request) {
		if(future.isSuccess()) {
			if(!future.getNow()) {
				ctx.fireChannelRead(request);
				readPending.forEach(msg->ctx.fireChannelRead(msg.msg()));
				readPending=null;
			}else {
				ReferenceCountUtil.release(request);
				readPending.successDiscard();
				readPending=null;
				this.discardPerpetually = true;
			}
		}else {
			ReferenceCountUtil.release(request);
			readPending.successDiscard();
			ctx.fireExceptionCaught(future.cause());
			readPending=null;
			this.discardPerpetually = true;
		}
	}

}
