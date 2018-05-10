package com.simplyti.service.channel.handler.inits;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map.Entry;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public abstract class HandlerInit {
	
	public List<String> canHandle(ChannelHandlerContext ctx, HttpRequest request, String fromHandler) {
		Deque<Entry<String, ChannelHandler>> handlers = canHandle0(request);
		if(handlers!=null) {
			return addHandlers(ctx, fromHandler, handlers);
		}else {
			return null;
		}
	}
	
	private List<String> addHandlers(ChannelHandlerContext ctx, String fromHandler, Deque<Entry<String, ChannelHandler>> handlers) {
		List<String> added = new ArrayList<>(handlers.size());
		String after = fromHandler;
		for(Entry<String,ChannelHandler> handler:handlers) {
			ctx.pipeline().addAfter(after, handler.getKey(), handler.getValue());
			after=handler.getKey();
			added.add(handler.getKey());
		}
		return added;
	}
	
	protected abstract Deque<Entry<String, ChannelHandler>> canHandle0(HttpRequest request);
	
}
