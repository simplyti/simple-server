package com.simplyti.server.http.api.context;

import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.api.ApiResponse;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;

public class ResponseTypedWithBodyApiContextImpl<T> extends AbstractApiContext implements ResponseTypedWithBodyApiContext<T> {
	
	private final ChannelHandlerContext ctx;
	private final ExceptionHandler exceptionHandler;
	private final boolean isKeepAlive;
	private final ByteBuf body;
	
	public ResponseTypedWithBodyApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request, ByteBuf body, ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx.channel(), request, match);
		this.ctx=ctx;
		this.isKeepAlive=HttpUtil.isKeepAlive(request);
		this.body=body;
		this.exceptionHandler=exceptionHandler;
	}

	@Override
	public Future<Void> writeAndFlush(T value) {
		body.release();
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(value, isKeepAlive, false))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}
	
	@Override
	public Future<Void> failure(Throwable cause) {
		body.release();
		return exceptionHandler.exceptionCaught(ctx, cause);
	}
	
	private void writeListener(io.netty.util.concurrent.Future<? super Void> future) {
		if(future.isSuccess()) {
			if(!isKeepAlive) {
				ctx.channel().close();
			}
		}
	}

	@Override
	public Future<Void> send(T value) {
		return writeAndFlush(value);
	}

	@Override
	public Future<Void> close() {
		body.release();
		return new DefaultFuture<>(ctx.close(),ctx.executor());
	}

	@Override
	public ByteBuf body() {
		return body;
	}

}
