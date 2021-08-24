package com.simplyti.server.http.api.context;

import com.simplyti.server.http.api.builder.stream.ChunkedResponseContextConsumer;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class AnyWithBodyApiContextImpl extends AbstractWithBodyApiContext<Object> implements AnyWithBodyApiContext {
	
	private final ByteBuf body;
	
	public AnyWithBodyApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request, ByteBuf body, ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx, request, match, exceptionHandler, ()->body.release());
		this.body=body;
	}

	public ByteBuf body() {
		return body;
	}

	@Override
	public Future<Void> sendChunked(ChunkedResponseContextConsumer consumer) {
		release();
		return super.sendChunked(consumer);
	}

}
