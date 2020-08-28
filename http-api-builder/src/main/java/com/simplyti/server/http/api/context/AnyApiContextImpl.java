package com.simplyti.server.http.api.context;

import com.simplyti.server.http.api.builder.stream.StreamedResponseContextConsumer;
import com.simplyti.server.http.api.context.stream.StreamedResponseContextImpl;
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
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

public class AnyApiContextImpl extends AbstractApiContext implements AnyApiContext {
	
	private final ChannelHandlerContext ctx;
	private final boolean isKeepAlive;
	private final ExceptionHandler exceptionHandler;
	private final ApiOperation<?> operation;

	public AnyApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, 
			ChannelHandlerContext ctx, HttpRequest request,ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx, request, match);
		this.ctx=ctx;
		this.isKeepAlive=HttpUtil.isKeepAlive(request);
		this.exceptionHandler=exceptionHandler;
		this.operation = match.operation();
	}

	@Override
	public Future<Void> close() {
		return new DefaultFuture<>(ctx.close(),ctx.executor());
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
	public Future<Void> failure(Throwable cause) {
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
	public Future<Void> sendEmpty() {
		return writeAndFlushEmpty();
	}


	@Override
	public Future<Void> send(Object value) {
		return writeAndFlush(value);
	}
	
	@Override
	public Future<Void> sendStreamed(StreamedResponseContextConsumer consumer) {
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
	    response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
	    return writeAndFlush(response).thenAccept(f->consumer.accept(new StreamedResponseContextImpl(ctx)));
	}

}
