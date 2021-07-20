package com.simplyti.server.http.api.context.fileupload;

import com.simplyti.server.http.api.context.AbstractApiContext;
import com.simplyti.server.http.api.handler.MultipartApiInvocationHandler;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;
import com.simplyti.util.concurrent.ThrowableConsumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.concurrent.Promise;

public class FileUploadAnyApiContextImpl extends AbstractApiContext<Object> implements FileUploadAnyApiContext {
	
	private final ChannelHandlerContext ctx;
	
	public FileUploadAnyApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request, ApiMatchRequest matcher) {
		super(syncTaskSubmitter, ctx, request, matcher, exceptionHandler);
		this.ctx=ctx;
	}

	@Override
	public Future<Void> eachPart(ThrowableConsumer<InterfaceHttpData> consumer) {
		Promise<Void> promise = ctx.executor().newPromise();
		ctx.pipeline().addAfter("api-multipart-decoder","multipart-input",new MultipartApiInvocationHandler(request(),consumer,promise));
		return new DefaultFuture<>(promise, ctx.executor());
	}

}
