package com.simplyti.service.clients.pending;

import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class PendingMessages {
	
	private PendingMessage head;
	private PendingMessage tail;

	public void pending(Promise<Void> future, Object msg) {
		PendingMessage message = PendingMessage.newInstance(future,ReferenceCountUtil.retain(msg));
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
		if (isEmpty()) {
			return channel.eventLoop().newSucceededFuture(null);
		}
		
		Promise<Void> promise = channel.eventLoop().newPromise();
		PromiseCombiner combiner = new PromiseCombiner();
		PendingMessage write = head;
		head = tail = null;
		while (write != null) {
			PendingMessage next = write.next();
			Object msg = write.msg();
			Promise<Void> writePromise = write.promise();
			write.recycle();
			combiner.add(channel.pipeline().writeAndFlush(msg).addListener(f->{
				if(f.isSuccess()) {
					writePromise.setSuccess(null);
				}else {
					writePromise.setFailure(f.cause());
				}
			}));
			write = next;
		}
		combiner.finish(promise);
		return promise;
	}

}
