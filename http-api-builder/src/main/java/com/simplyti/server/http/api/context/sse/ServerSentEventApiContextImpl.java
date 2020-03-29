package com.simplyti.server.http.api.context.sse;

import com.simplyti.server.http.api.context.AbstractApiContext;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.server.http.api.sse.ServerEvent;
import com.simplyti.server.http.api.sse.ServerSentEventEncoder;
import com.simplyti.service.channel.handler.ClientChannelHandler;
import com.simplyti.service.commons.netty.pending.PendingMessages;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Promise;

public class ServerSentEventApiContextImpl extends AbstractApiContext implements ServerSentEventAnyApiContext {

	private static final String EVENT_STREAM = "text/event-stream";
	
	private enum UpdateStatus {
		INIT,UPDATING,UPDATED, FAIL;
	}
	
	private final ChannelHandlerContext ctx;
	private final ServerSentEventEncoder serverEventEncoder;
	private final ExceptionHandler exceptionHandler;
	
	private UpdateStatus status = UpdateStatus.INIT;
	private PendingMessages pendingMessages;
	private Throwable updateError;
	
	public ServerSentEventApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request,
			ApiMatchRequest matcher, ServerSentEventEncoder serverEventEncoder) {
		super(syncTaskSubmitter, ctx.channel(), request, matcher);
		this.ctx=ctx;
		this.serverEventEncoder=serverEventEncoder;
		this.exceptionHandler=exceptionHandler;
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
	public Future<Void> send(String data) {
		return send(new ServerEvent(null,null,data));
	}

	private Future<Void> send(ServerEvent event) {
		if(status == UpdateStatus.INIT) {
			this.pendingMessages = new PendingMessages();
			status = UpdateStatus.UPDATING;
			sendUpdateResponse();
			return addPending(event);
		} else if (status == UpdateStatus.UPDATING) {
			return addPending(event);
		} else if (status == UpdateStatus.UPDATED){
			ChannelFuture future = ctx.writeAndFlush(event);
			return new DefaultFuture<>(future,ctx.executor());
		} else {
			return new DefaultFuture<>(ctx.newFailedFuture(updateError),ctx.executor());
		}
	}
	
	private void sendUpdateResponse() {
		HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, EVENT_STREAM);
		ctx.writeAndFlush(response)
				.addListener(f-> {
					if(f.isSuccess()) {
						ctx.pipeline().remove("encoder");
						ctx.pipeline().remove("decoder");
						ctx.pipeline().addBefore(ClientChannelHandler.NAME,"sse-codec", serverEventEncoder);
						pendingMessages.write(ctx.channel());
						status = UpdateStatus.UPDATED;
					} else {
						updateError = f.cause();
						pendingMessages.fail(updateError);
						status = UpdateStatus.FAIL;
					}
				});
	}

	private Future<Void> addPending(ServerEvent event) {
		Promise<Void> promise = ctx.executor().newPromise();
		if(ctx.executor().inEventLoop()) {
			addPending0(promise,event);
		}else {
			ctx.executor().submit(()->addPending0(promise,event));
		}
		return new DefaultFuture<>(promise,ctx.executor());
	}
	
	private void addPending0(Promise<Void> promise, ServerEvent event) {
		if(status == UpdateStatus.UPDATED) {
			ctx.writeAndFlush(event).addListener(f->toPromise(f,promise));
		} else if (status == UpdateStatus.FAIL) {
			promise.setFailure(updateError);
		}else {
			pendingMessages.pending(promise,event);
		}
	}
	
	private void toPromise(io.netty.util.concurrent.Future<? super Void> f, Promise<Void> promise) {
		if(f.isSuccess()) {
			promise.setSuccess(null);
		}else {
			promise.setFailure(f.cause());
		}
	}
	
}
