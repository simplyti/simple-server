package com.simplyti.server.http.api.handler.init;

import java.util.Collection;

import com.simplyti.server.http.api.filter.OperationInboundFilter;
import com.simplyti.server.http.api.handler.MultipartApiInvocationHandler;
import com.simplyti.server.http.api.operations.FileUploadApiOperation;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;

public class MultipartApiHandler extends SimpleChannelInboundHandler<HttpRequest> {
	
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;

	private ApiMatchRequest matchRequest;
	
	public MultipartApiHandler(ExceptionHandler exceptionHandler, SyncTaskSubmitter syncTaskSubmitter, Collection<OperationInboundFilter> filters) {
		super(false);
		this.exceptionHandler=exceptionHandler;
		this.syncTaskSubmitter=syncTaskSubmitter;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		if(matchRequest!=null) {
			ctx.pipeline().addAfter("api-multipart-decoder","multipart-input",new MultipartApiInvocationHandler(msg,exceptionHandler,syncTaskSubmitter,matchRequest));
			this.matchRequest = null;
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof ApiMatchRequest) {
			if(((ApiMatchRequest) evt).operation() instanceof FileUploadApiOperation) {
				this.matchRequest=(ApiMatchRequest) evt;
			} else {
				ctx.fireUserEventTriggered(evt);
			}
		} else {
			ctx.fireUserEventTriggered(evt);
		}
    }

}
