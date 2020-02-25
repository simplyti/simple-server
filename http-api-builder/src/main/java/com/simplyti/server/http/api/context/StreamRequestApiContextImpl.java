package com.simplyti.server.http.api.context;

import java.util.function.Consumer;

import com.simplyti.server.http.api.handler.StreamedApiInvocationHandler;
import com.simplyti.server.http.api.request.ApiMatchRequest;
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

	private final ChannelHandlerContext ctx;
	private final boolean isKeepAlive;

	public StreamRequestApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ChannelHandlerContext ctx, HttpRequest request,
			ApiMatchRequest matcher) {
		super(syncTaskSubmitter, ctx.channel(), request, matcher);
		this.ctx=ctx;
		this.isKeepAlive=HttpUtil.isKeepAlive(request);
	}

	@Override
	public Future<Void> writeAndFlushEmpty() {
		return null;
	}

	@Override
	public Future<Void> writeAndFlush(String message) {
		return null;
	}

	@Override
	public Future<Void> writeAndFlush(ByteBuf body) {
		return null;
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
		return null;
	}

	@Override
	public Future<Void> sendEmpty() {
		return null;
	}

	@Override
	public Future<Void> send(String string) {
		return null;
	}

	@Override
	public Future<Void> send(ByteBuf body) {
		return null;
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
		return null;
	}

	@Override
	public Future<Void> failure(Throwable cause) {
		return null;
	}

	@Override
	public Future<Void> close() {
		return null;
	}

	@Override
	public Future<Void> stream(Consumer<ByteBuf> consumer) {
		Promise<Void> promise = ctx.executor().newPromise();
		ctx.pipeline().addLast(new StreamedApiInvocationHandler(consumer,promise));
		return new DefaultFuture<>(promise, ctx.executor());
	}

}
