package com.simplyti.service.sse;

import com.simplyti.service.clients.pending.PendingMessages;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class DefaultSSEStream implements SSEStream {

	private final ChannelHandlerContext ctx;
	private final PendingMessages pendingMessages;
	
	private boolean updated = false;
	
	public DefaultSSEStream(ChannelHandlerContext ctx) {
		this.ctx=ctx;
		this.pendingMessages=new PendingMessages();
		HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
		ctx.writeAndFlush(response)
				.addListener(f->updateConnection(f));
	}

	private void updateConnection(Future<? super Void> f) {
		if(f.isSuccess()) {
			ctx.pipeline().remove(HttpServerCodec.class);
		}else {
			pendingMessages.fail(f.cause());
		}
		updated=true;
	}

	@Override
	public Future<Void> send(String data) {
		if(updated) {
			return send0(data);
		}else {
			return addPending(data);
		}
	}

	private Future<Void> addPending(String data) {
		Promise<Void> promise = ctx.executor().newPromise();
		if(ctx.executor().inEventLoop()) {
			addPending0(promise,data);
		}else {
			ctx.executor().submit(()->addPending0(promise,data));
		}
		return promise;
	}
	
	private void addPending0(Promise<Void> promise, String msg) {
		if(updated) {
			send0(msg).addListener(f->toPromise(f,promise));
		}else {
			pendingMessages.pending(promise,msg);
		}
	}
	
	private void toPromise(Future<? super Void> f, Promise<Void> promise) {
		if(f.isSuccess()) {
			promise.setSuccess(null);
		}else {
			promise.setFailure(f.cause());
		}
	}

	private Future<Void> send0(String data) {
		ByteBuf buffer = ctx.alloc().buffer();
		buffer.writeCharSequence("data:", CharsetUtil.UTF_8);
		buffer.writeCharSequence(data, CharsetUtil.UTF_8);
		buffer.writeCharSequence("\n\n", CharsetUtil.UTF_8);
		return ctx.writeAndFlush(buffer);
	}

}
