package com.simplyti.service.channel.handler;

import java.util.List;

import com.simplyti.service.channel.pending.PendingMessages;
import com.simplyti.service.filter.FilterChain;
import com.simplyti.service.filter.http.HttpResponseFilter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class HttpResponseFilterHandler extends ChannelOutboundHandlerAdapter {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(ClientChannelHandler.class);

	private final List<HttpResponseFilter> filters;
	
	private PendingMessages writePending;
	private boolean flushed;

	public HttpResponseFilterHandler(List<HttpResponseFilter> filters) {
		this.filters=filters;
	}
	
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) msg;
			Future<Boolean> futureHandled = FilterChain.of(filters, ctx, response).execute();
			this.writePending=new PendingMessages();
			futureHandled.addListener(f->handleResponseFilter(futureHandled,ctx,response,promise));
		} else if(writePending!=null) {
			this.writePending.pending(msg,promise);
		} else {
			ctx.write(msg, promise);
		}
    }
	
	@Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
		if(writePending==null) {
			ctx.flush();
		} else {
			this.flushed=true;
		}
    }
	
	private void handleResponseFilter(Future<Boolean> future, ChannelHandlerContext ctx, HttpResponse response, ChannelPromise promise) {
		if(!future.isSuccess()) {
			log.warn("Error executing response filters: {}",future.cause().getMessage());
		}
		ctx.write(response, promise);
		PendingMessages thisWritePending = writePending;
		writePending=null;
		thisWritePending.forEach(msg->ctx.write(msg.msg(), msg.promise()));
		if(flushed) {
			ctx.flush();
		}
	}


}
