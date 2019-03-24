package com.simplyti.service.commons.pending;

import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class PendingMessages {
	
	private PendingMessage head;
	private PendingMessage tail;
	
	public void pending(Promise<Void> future, Object msg) {
		PendingMessage message = PendingMessage.newInstance(future,msg);
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
			write(channel,promise);
		}else {
			channel.eventLoop().execute(()->write(channel,promise));
		}
		return promise;
	}

	private void write(Channel channel, Promise<Void> promise) {
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
			combiner.add(write(channel,msg,writePromise));
			write = next;
		}
		combiner.finish(promise);
	}

	private Future<Void> write(Channel channel, Object msg, Promise<Void> writePromise) {
		channel.pipeline().writeAndFlush(msg).addListener(f->{
			if(f.isSuccess()) {
				writePromise.setSuccess(null);
			}else {
				writePromise.setFailure(f.cause());
			}
		});
		return writePromise;
	}

}
