package com.simplyti.server.http.api.context.stream;

import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class StreamedResponseContextImpl implements StreamedResponseContext {

	private final ChannelHandlerContext ctx;
	
	private boolean finished;

	public StreamedResponseContextImpl(ChannelHandlerContext ctx) {
		this.ctx=ctx;
	}

	@Override
	public Future<Void> send(String data) {
		if(finished) {
			return new DefaultFuture<>(ctx.executor().newFailedFuture(new IllegalStateException("Already finished")),ctx.executor());
		}
		return new DefaultFuture<>(ctx.writeAndFlush(new DefaultHttpContent(Unpooled.copiedBuffer(data, CharsetUtil.UTF_8))),ctx.executor());
	}

	@Override
	public Future<Void> finish() {
		if(finished) {
			return new DefaultFuture<>(ctx.executor().newFailedFuture(new IllegalStateException("Already finished")),ctx.executor());
		}
		finished = true;
		return new DefaultFuture<>(ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT),ctx.executor());
	}

}
