package com.simplyti.service.clients.http.handler;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.simplyti.service.clients.channel.ClientChannelEvent;
import com.simplyti.service.clients.http.request.HttpRequestFilterEvent;
import com.simplyti.service.commons.netty.pending.PendingMessages;
import com.simplyti.service.filter.FilterChain;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.service.filter.priority.Priorized;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;

public class HttpRequestFilterHandler extends ChannelOutboundHandlerAdapter implements ChannelInboundHandler {

	private List<HttpRequestFilter> requestFilter;
	
	private final List<HttpRequestFilter> filters;
	
	private PendingMessages pending;
	private boolean discardPerpetually;
	private Throwable error;

	public HttpRequestFilterHandler(List<HttpRequestFilter> filters) {
		this.filters=filters;
	}
	
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(noFilters()) {
			ctx.write(msg, promise);
		} else if(discardPerpetually) {
			ReferenceCountUtil.release(msg);
			if(error!=null) {
				promise.setFailure(error);
			} else {
				promise.setSuccess(null);
			}
			if(msg instanceof LastHttpContent) {
				this.discardPerpetually = false;
			}
		} else if(msg instanceof HttpRequest) {
			Future<Boolean> futureHandled = FilterChain.of(filters(), ctx, (HttpRequest) msg).execute();
			this.pending=new PendingMessages();
			this.pending.pending(promise,msg);
			futureHandled.addListener(f->handleRequestFilter(futureHandled,ctx, msg));
		} else if(pending!=null) {
			this.pending.pending(promise,msg);
		} else {
			ctx.write(msg, promise);
		}
    }
	
	private Collection<HttpRequestFilter> filters() {
		if(filters == null) {
			return requestFilter;
		} else if(requestFilter == null) {
			return filters;
		} else {
			return Stream.concat(filters.stream(), requestFilter.stream())
					.sorted(Priorized.PRIORITY_ANN_ORDER)
					.collect(Collectors.toList());
		}
	}

	private boolean noFilters() {
		return (filters == null || filters.isEmpty()) && (requestFilter == null || requestFilter.isEmpty());
	}

	private void handleRequestFilter(Future<Boolean> future, ChannelHandlerContext ctx, Object msg) {
		if(future.isSuccess()) {
			if(!future.getNow()) {
				PendingMessages thisPending = pending;
				pending=null;
				thisPending.write(ctx);
			}else {
				PendingMessages thisPending = pending;
				pending=null;
				if(!(msg instanceof LastHttpContent)) {
					this.discardPerpetually = true;
				}
				thisPending.successDiscard();
			}
		} else {
			PendingMessages thisPending = pending;
			pending=null;
			if(!(msg instanceof LastHttpContent)) {
				this.discardPerpetually = true;
			}
			this.error = future.cause();
			thisPending.fail(future.cause());
		}
	}

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	if(evt instanceof HttpRequestFilterEvent) {
    		this.requestFilter = ((HttpRequestFilterEvent) evt).filters();
    	} else {
    		if(evt == ClientChannelEvent.RELEASED) {
    			this.requestFilter = null;
    		}
    		ctx.fireUserEventTriggered(evt);
    	}
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  throws Exception {
        ctx.fireExceptionCaught(cause);
    }

}
