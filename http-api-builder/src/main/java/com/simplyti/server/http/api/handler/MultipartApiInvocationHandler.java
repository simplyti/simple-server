package com.simplyti.server.http.api.handler;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.fileupload.FileUploadAnyApiContextImpl;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;

public class MultipartApiInvocationHandler extends SimpleChannelInboundHandler<HttpContent> {

	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	private final HttpRequest request;
	private final ApiMatchRequest matchRequest;
	private final HttpPostMultipartRequestDecoder decode;
	

	public MultipartApiInvocationHandler(HttpRequest request, ExceptionHandler exceptionHandler, SyncTaskSubmitter syncTaskSubmitter, ApiMatchRequest matchRequest) {
		this.request=request;
		this.exceptionHandler=exceptionHandler;
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.matchRequest=matchRequest;
		this.decode=new HttpPostMultipartRequestDecoder(new DefaultHttpDataFactory(true),request);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
		decode.offer(msg);
		if(msg instanceof LastHttpContent) {
			serviceProceed(matchRequest.operation(), context(ctx));
			ctx.pipeline().remove(this);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends ApiContext> T context(ChannelHandlerContext ctx) {
		return (T) new FileUploadAnyApiContextImpl(syncTaskSubmitter, exceptionHandler, ctx, request, matchRequest, decode);
	}

	private <T extends ApiContext> void serviceProceed(ApiOperation<T> operation, T ctx) {
		try{
			operation.handler().accept(ctx);
		}catch(Throwable e) {
			ctx.failure(e);
		}
	}
}
