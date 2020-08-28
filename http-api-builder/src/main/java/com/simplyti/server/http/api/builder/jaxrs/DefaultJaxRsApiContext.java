package com.simplyti.server.http.api.builder.jaxrs;

import com.simplyti.server.http.api.context.AbstractApiContext;
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

public class DefaultJaxRsApiContext<T> extends AbstractApiContext implements JaxRsApiContext<T> {

	private final ExceptionHandler exceptionHandler;
	private final ChannelHandlerContext ctx;
	private final boolean isKeepAlive;
	private final Object body;

	public DefaultJaxRsApiContext(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, 
			ChannelHandlerContext ctx, HttpRequest request,ApiMatchRequest match, Object body) {
		super(syncTaskSubmitter, ctx, request, match);
		this.ctx=ctx;
		this.exceptionHandler=exceptionHandler;
		this.isKeepAlive=HttpUtil.isKeepAlive(request);
		this.body=body;
	}

	@Override
	public Future<Void> writeAndFlush(T value) {
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(value, isKeepAlive, false))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}
	
	private void writeListener(io.netty.util.concurrent.Future<? super Void> future) {
		if(future.isSuccess()) {
			if(!isKeepAlive) {
				ctx.channel().close();
			}
		}
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
	public Future<Void> send(T value) {
		return writeAndFlush(value);
	}

	@Override
	public Object body() {
		return body;
	}

}
