package com.simplyti.server.http.api.context;

import java.nio.channels.ClosedChannelException;

import com.simplyti.server.http.api.handler.message.ApiResponse;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;

public class AnyWithBodyApiContextImpl extends AbstractApiContext implements AnyWithBodyApiContext {
	
	private final ChannelHandlerContext ctx;
	private final ExceptionHandler exceptionHandler;
	private final boolean isKeepAlive;
	private final ByteBuf body;
	private final ApiOperation<?> operation;
	
	private boolean released;

	public AnyWithBodyApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request, ByteBuf body, ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx.channel(), request, match);
		this.ctx=ctx;
		this.exceptionHandler=exceptionHandler;
		this.isKeepAlive=HttpUtil.isKeepAlive(request);
		this.body=body;
		this.operation=match.operation();
	}

	@Override
	public Future<Void> writeAndFlush(String message) {
		release();
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(message, isKeepAlive, operation.notFoundOnNull()))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}
	
	@Override
	public Future<Void> writeAndFlush(Object value) {
		release();
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(value, isKeepAlive, operation.notFoundOnNull()))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}

	@Override
	public Future<Void> writeAndFlush(ByteBuf value) {
		release();
		if(!ctx.channel().isActive()) {
			ReferenceCountUtil.release(value);
			return new DefaultFuture<>(ctx.channel().newFailedFuture(new ClosedChannelException()),ctx.executor());
		}
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(value, isKeepAlive, operation.notFoundOnNull()))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}
	
	@Override
	public Future<Void> writeAndFlushEmpty() {
		release();
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(null, isKeepAlive, operation.notFoundOnNull()))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}
	
	@Override
	public Future<Void> writeAndFlush(HttpObject response) {
		release();
		try {
			ChannelFuture future = ctx.writeAndFlush(response)
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}
	
	@Override
	public Future<Void> failure(Throwable cause) {
		release();
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
	public void release() {
		if(this.released) {
			return;
		}
		body.release();
		this.released=true;
	}

	@Override
	public Future<Void> close() {
		release();
		return new DefaultFuture<>(ctx.close(),ctx.executor());
	}

	@Override
	public ByteBuf body() {
		return body;
	}

	@Override
	public Future<Void> send(String message) {
		return writeAndFlush(message);
	}

	@Override
	public Future<Void> send(ByteBuf body) {
		return writeAndFlush(body);
	}

	@Override
	public Future<Void> send(HttpObject response) {
		return writeAndFlush(response);
	}
	
	@Override
	public Future<Void> send(Object value) {
		return writeAndFlush(value);
	}

	@Override
	public Future<Void> sendEmpty() {
		return writeAndFlushEmpty();
	}

}
