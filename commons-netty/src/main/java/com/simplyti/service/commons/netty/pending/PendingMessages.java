package com.simplyti.service.commons.netty.pending;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class PendingMessages {
	
	private PendingMessage head;
	private PendingMessage tail;
	
	public void pending(Promise<Void> future, Object msg) {
		PendingMessage message = PendingMessage.newInstance(future, msg);
		PendingMessage currentTail = tail;
		if (currentTail == null) {
			tail = head = message;
		} else {
			currentTail.next(message);
			tail = message;
		}
	}

	public void fail(Throwable cause) {
		if (isEmpty()) {
			return;
		}
		
		PendingMessage write = head;
		head = tail = null;
		while (write != null) {
			PendingMessage next = write.next();
			Promise<Void> promise = write.promise();
			ReferenceCountUtil.release(write.msg());
			write.recycle();
			promise.setFailure(cause);
			write = next;
		}
	}
	
	public boolean isEmpty() {
		return head == null;
	}

	public Future<Void> write(Channel channel) {
		Promise<Void> promise = channel.newPromise();
		if(channel.eventLoop().inEventLoop()) {
			write(null,channel,promise);
		}else {
			channel.eventLoop().execute(()->write(null,channel,promise));
		}
		return promise;
	}
	
	public Future<Void> write(ChannelHandlerContext ctx) {
		Promise<Void> promise = ctx.newPromise();
		if(ctx.executor().inEventLoop()) {
			write(ctx,ctx.channel(),promise);
		}else {
			ctx.executor().execute(()->write(ctx,ctx.channel(),promise));
		}
		return promise;
	}

	private void write(ChannelHandlerContext ctx, Channel channel, Promise<Void> promise) {
		if (isEmpty()) {
			promise.setSuccess(null);
		}
		
		PromiseCombiner combiner = new PromiseCombiner(channel.eventLoop());
		PendingMessage write = head;
		head = tail = null;
		while (write != null) {
			PendingMessage next = write.next();
			Object msg = write.msg();
			Promise<Void> writePromise = write.promise();
			write.recycle();
			combiner.add(write(ctx,channel,msg,writePromise));
			write = next;
		}
		combiner.finish(promise);
	}

	private Future<Void> write(ChannelHandlerContext ctx, Channel channel, Object msg, Promise<Void> writePromise) {
		(ctx!=null? ctx.writeAndFlush(msg) : channel.writeAndFlush(msg))
			.addListener(f->{
			if(f.isSuccess()) {
				writePromise.setSuccess(null);
			}else {
				writePromise.setFailure(f.cause());
			}
		});
		return writePromise;
	}

	public void successDiscard() {
		PendingMessage write = head;
		head = tail = null;
		while (write != null) {
			PendingMessage next = write.next();
			ReferenceCountUtil.release(write.msg());
			write.promise().setSuccess(null);
			write = next;
		}
	}

}
