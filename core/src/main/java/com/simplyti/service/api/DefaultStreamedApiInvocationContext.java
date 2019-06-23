package com.simplyti.service.api;

import java.util.function.Consumer;

import com.simplyti.service.channel.handler.StreamedApiInvocationHandler;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class DefaultStreamedApiInvocationContext<T> extends AbstractApiInvocationContext<T> implements StreamedApiInvocationContext<T>{

	private final ChannelHandlerContext ctx;
	private final StreamedApiInvocationHandler streamHandler;

	public DefaultStreamedApiInvocationContext(ChannelHandlerContext ctx,ApiMacher matcher, StreamedApiInvocation msg, 
			ExceptionHandler exceptionHandler, SyncTaskSubmitter syncTaskSubmitter, StreamedApiInvocationHandler streamHandler) {
		super(ctx,matcher, msg,exceptionHandler,syncTaskSubmitter);
		this.ctx=ctx;
		this.streamHandler=streamHandler;
	}
	
	@Override
	public Future<Void> stream(Consumer<ByteBuf> consumer) {
		Promise<Void> promise = ctx.channel().eventLoop().newPromise();
		streamHandler.setDataConsumer(consumer,promise);
		return promise;
	}

}
