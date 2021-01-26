package com.simplyti.server.http.api.context.chunked;

import com.simplyti.service.commons.netty.Promises;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

public class ChunkedResponseContextImpl implements ChunkedResponseContext {

	private final ChannelHandlerContext ctx;
	
	private boolean finished;

	public ChunkedResponseContextImpl(ChannelHandlerContext ctx) {
		this.ctx=ctx;
	}

	@Override
	public Future<Void> send(String data) {
		if(ctx.executor().inEventLoop()) {
			return send0(Unpooled.copiedBuffer(data, CharsetUtil.UTF_8), null);
		} else {
			Promise<Void> promise = ctx.executor().newPromise();
			ctx.executor().execute(()->send0(Unpooled.copiedBuffer(data, CharsetUtil.UTF_8),promise));
			return new DefaultFuture<>(promise,ctx.executor());
		}
	}
	
	@Override
	public Future<Void> send(ByteBuf data) {
		if(ctx.executor().inEventLoop()) {
			return send0(data, null);
		} else {
			Promise<Void> promise = ctx.executor().newPromise();
			ctx.executor().execute(()->send0(data,promise));
			return new DefaultFuture<>(promise,ctx.executor());
		}
	}

	private Future<Void> send0(ByteBuf data, Promise<Void> promise) {
		if(finished) {
			data.release();
			Throwable cause = new IllegalStateException("Already finished");
			if(promise == null) {
				return new DefaultFuture<>(ctx.executor().newFailedFuture(cause),ctx.executor());
			} else {
				promise.setFailure(cause);
				return null;
			}
		} else {
			if(promise == null) {
				return new DefaultFuture<>(ctx.writeAndFlush(new DefaultHttpContent(data)),ctx.executor());
			} else {
				Promises.toPromise(ctx.writeAndFlush(new DefaultHttpContent(data)), promise);
				return null;
			}
		}
	}

	@Override
	public Future<Void> finish() {
		if(ctx.executor().inEventLoop()) {
			return finish0(null);
		} else {
			Promise<Void> promise = ctx.executor().newPromise();
			ctx.executor().execute(()->finish0(promise));
			return new DefaultFuture<>(promise,ctx.executor());
		}
		
	}

	private Future<Void> finish0(Promise<Void> promise) {
		if(finished) {
			if(promise == null) {
				return new DefaultFuture<>(ctx.executor().newSucceededFuture(null),ctx.executor());
			} else {
				promise.setSuccess(null);
				return null;
			}
		} else {
			finished = true;
			if(promise == null) {
				return new DefaultFuture<>(ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT),ctx.executor());
			} else {
				Promises.toPromise(ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT), promise);
				return null;
			}
		}
	}

}
