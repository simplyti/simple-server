package com.simplyti.service.sse;

import com.simplyti.service.clients.pending.PendingMessages;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class DefaultSSEStream implements SSEStream {

	private final ChannelHandlerContext ctx;
	private final PendingMessages pendingMessages;
	
	private boolean updated = false;
	
	public DefaultSSEStream(ChannelHandlerContext ctx,ServerSentEventEncoder serverEventEncoder) {
		this.ctx=ctx;
		this.pendingMessages=new PendingMessages();
		HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
		ctx.writeAndFlush(response)
				.addListener(f->updateConnection(f,serverEventEncoder));
	}

	private void updateConnection(Future<? super Void> f, ServerSentEventEncoder serverEventEncoder) {
		if(f.isSuccess()) {
			ctx.pipeline().replace(HttpServerCodec.class, "sse-codec", serverEventEncoder);
		}else {
			pendingMessages.fail(f.cause());
		}
		updated=true;
	}

	@Override
	public Future<Void> send(String data) {
		return send(new ServerEvent(null,null,data));
	}
	
	@Override
	public Future<Void> send(String event, String data) {
		return send(new ServerEvent(event,null,data));
	}
	
	private Future<Void> send(ServerEvent event) {
		if(updated) {
			return ctx.writeAndFlush(event);
		}else {
			return addPending(event);
		}
	}

	private Future<Void> addPending(ServerEvent event) {
		Promise<Void> promise = ctx.executor().newPromise();
		if(ctx.executor().inEventLoop()) {
			addPending0(promise,event);
		}else {
			ctx.executor().submit(()->addPending0(promise,event));
		}
		return promise;
	}
	
	private void addPending0(Promise<Void> promise, ServerEvent event) {
		if(updated) {
			ctx.writeAndFlush(event).addListener(f->toPromise(f,promise));
		}else {
			pendingMessages.pending(promise,event);
		}
	}
	
	private void toPromise(Future<? super Void> f, Promise<Void> promise) {
		if(f.isSuccess()) {
			promise.setSuccess(null);
		}else {
			promise.setFailure(f.cause());
		}
	}

}
