package com.simplyti.service.channel.handler.inits;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public abstract class HandlerInit {
	
	public List<String> canHandle(ChannelHandlerContext ctx, HttpRequest request, String fromHandler) {
		ChannelHandlerEntry[] handlers = canHandle0(request);
		if(handlers!=null) {
			return addHandlers(ctx, fromHandler, handlers);
		}else {
			return null;
		}
	}
	
	private List<String> addHandlers(ChannelHandlerContext ctx, String fromHandler,ChannelHandlerEntry[] handlers) {
		List<String> added = new ArrayList<>(handlers.length);
		String after = fromHandler;
		for(ChannelHandlerEntry handler:handlers) {
			ctx.pipeline().addAfter(after, handler.name(), handler.handler());
			after=handler.name();
			added.add(handler.name());
		}
		return added;
	}
	
	protected abstract ChannelHandlerEntry[] canHandle0(HttpRequest request);
	
}
