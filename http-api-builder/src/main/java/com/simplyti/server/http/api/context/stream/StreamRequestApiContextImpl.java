package com.simplyti.server.http.api.context.stream;

import java.util.function.Consumer;

import com.simplyti.server.http.api.builder.stream.StreamedResponseContextConsumer;
import com.simplyti.server.http.api.context.AbstractApiContext;
import com.simplyti.server.http.api.handler.StreamedApiInvocationHandler;
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
import io.netty.util.concurrent.Promise;

public class StreamRequestApiContextImpl extends AbstractApiContext implements StreamdRequestApiContext {

	private final ExceptionHandler exceptionHandler;
	private final ChannelHandlerContext ctx;
	private final boolean isKeepAlive;
	private final ApiOperation<?> operation;

	public StreamRequestApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request,
			ApiMatchRequest matcher) {
		super(syncTaskSubmitter, ctx, request, matcher);
		this.exceptionHandler=exceptionHandler;
		this.ctx=ctx;
		this.isKeepAlive=HttpUtil.isKeepAlive(request);
		this.operation=matcher.operation();
	}

	@Override
	public Future<Void> writeAndFlushEmpty() {
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(null, isKeepAlive, false))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}

	@Override
	public Future<Void> writeAndFlush(String message) {
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(message, isKeepAlive, operation.notFoundOnNull()))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}

	@Override
	public Future<Void> writeAndFlush(ByteBuf body) {
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(body, isKeepAlive, operation.notFoundOnNull()))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}

	@Override
	public Future<Void> writeAndFlush(HttpObject response) {
		try {
			ChannelFuture future = ctx.writeAndFlush(response)
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}

	@Override
	public Future<Void> writeAndFlush(Object value) {
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(value, isKeepAlive, operation.notFoundOnNull()))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}

	@Override
	public Future<Void> send(HttpObject response) {
		return writeAndFlush(response);
	}
	
	private void writeListener(io.netty.util.concurrent.Future<? super Void> future) {
		if(future.isSuccess()) {
			if(!isKeepAlive) {
				ctx.channel().close();
			}
		}
	}

	@Override
	public Future<Void> send(Object value) {
		return writeAndFlush(value);
	}
	
	@Override
	public Future<Void> sendEmpty() {
		return writeAndFlushEmpty();
	}

	@Override
	public Future<Void> send(String string) {
		return writeAndFlush(string);
	}

	@Override
	public Future<Void> send(ByteBuf body) {
		return writeAndFlush(body);
	}

	@Override
	public Future<Void> failure(Throwable cause) {
		return exceptionHandler.exceptionCaught(ctx, cause);
	}

	@Override
	public Future<Void> close() {
		return new DefaultFuture<>(ctx.close(),ctx.executor());
	}

	@Override
	public Future<Void> stream(Consumer<ByteBuf> consumer) {
		Promise<Void> promise = ctx.executor().newPromise();
		ctx.pipeline().addLast(new StreamedApiInvocationHandler(consumer,promise));
		return new DefaultFuture<>(promise, ctx.executor());
	}
	
	@Override
	public Future<Void> sendStreamed(StreamedResponseContextConsumer object) {
		return null;
	}

}
