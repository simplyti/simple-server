package com.simplyti.server.http.api.context.stream;

import java.util.function.Consumer;

import com.simplyti.server.http.api.context.AbstractApiContext;
import com.simplyti.server.http.api.handler.StreamedApiInvocationHandler;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.Promise;

public class StreamRequestApiContextImpl extends AbstractApiContext<Object> implements StreamdRequestApiContext {

	private final ChannelHandlerContext ctx;

	public StreamRequestApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request,
			ApiMatchRequest matcher) {
		super(syncTaskSubmitter, ctx, request, matcher, exceptionHandler);
		this.ctx=ctx;
	}


	@Override
	public Future<Void> stream(Consumer<ByteBuf> consumer) {
		Promise<Void> promise = ctx.executor().newPromise();
		ctx.pipeline().addAfter("api-streamed-req-handler","stream-input",new StreamedApiInvocationHandler(consumer,promise));
		return new DefaultFuture<>(promise, ctx.executor());
	}
	
}
