package com.simplyti.server.http.api.context.sse;

import com.simplyti.server.http.api.sse.ServerEvent;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.EventExecutor;

public class ServerSentEventApiContextImpl implements ServerSentEventApiContext {

	private final ChannelHandlerContext ctx;
	
	public ServerSentEventApiContextImpl(ChannelHandlerContext ctx) {
		this.ctx=ctx;
	}

	@Override
	public Future<Void> send(String data) {
		return send(new ServerEvent(null,null,data));
	}
	
	private Future<Void> send(ServerEvent event) {
		ChannelFuture future = ctx.writeAndFlush(event);
		return new DefaultFuture<>(future,ctx.executor());
	}

	@Override
	public EventExecutor executor() {
		return ctx.executor();
	}

}
