package com.simplyti.server.http.api.context;

import com.simplyti.server.http.api.handler.message.ApiResponse;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;

public class ResponseTypedApiContextImpl<T> extends AbstractApiContext implements ResponseTypedApiContext<T> {
	
	private final ChannelHandlerContext ctx;
	private final ExceptionHandler exceptionHandler;
	private final boolean isKeepAlive;
	private final boolean notFoundOnNull;
	
	public ResponseTypedApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request,ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx.channel(), request, match);
		this.ctx=ctx;
		this.exceptionHandler=exceptionHandler;
		this.isKeepAlive=HttpUtil.isKeepAlive(request);
		this.notFoundOnNull=match.operation().notFoundOnNull();
	}

	@Override
	public Future<Void> close() {
		return new DefaultFuture<>(ctx.close(),ctx.executor());
	}

	@Override
	public Future<Void> writeAndFlush(T value) {
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(value, isKeepAlive, notFoundOnNull))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}
	
	@Override
	public Future<Void> failure(Throwable cause) {
		return exceptionHandler.exceptionCaught(ctx, cause);
	}

	@Override
	public Future<Void> send(T value) {
		return writeAndFlush(value);
	}
	
	private void writeListener(io.netty.util.concurrent.Future<? super Void> future) {
		if(future.isSuccess()) {
			if(!isKeepAlive) {
				ctx.channel().close();
			}
		} else {
			failure(future.cause());
		}
	}
	
}
