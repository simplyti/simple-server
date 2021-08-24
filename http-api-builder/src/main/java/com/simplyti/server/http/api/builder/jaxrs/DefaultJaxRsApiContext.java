package com.simplyti.server.http.api.builder.jaxrs;

import com.simplyti.server.http.api.context.AbstractApiContext;
import com.simplyti.server.http.api.handler.message.ApiCharSequenceResponse;
import com.simplyti.server.http.api.handler.message.ApiObjectResponse;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;

public class DefaultJaxRsApiContext<T> extends AbstractApiContext<T> implements JaxRsApiContext<T> {

	private final ChannelHandlerContext ctx;
	private final boolean isKeepAlive;
	private final Object body;

	public DefaultJaxRsApiContext(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, 
			ChannelHandlerContext ctx, HttpRequest request,ApiMatchRequest match, Object body) {
		super(syncTaskSubmitter, ctx, request, match, exceptionHandler);
		this.ctx=ctx;
		this.isKeepAlive=HttpUtil.isKeepAlive(request);
		this.body=body;
	}

	@Override
	public Future<Void> writeAndFlush(T value) {
		try {
			ChannelFuture future = ctx.writeAndFlush(response(value))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(Exception cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}
	
	private Object response(T value) {
		if(value instanceof String) {
			return new ApiCharSequenceResponse((String) value, isKeepAlive, false);
		} else {
			return new ApiObjectResponse(value, isKeepAlive, false);
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
	public Future<Void> send(T value) {
		return writeAndFlush(value);
	}

	@Override
	public Object body() {
		return body;
	}

}
