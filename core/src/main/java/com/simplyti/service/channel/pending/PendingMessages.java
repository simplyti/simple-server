package com.simplyti.service.channel.pending;

import java.util.function.Consumer;

import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

public class PendingMessages {
	
	private PendingMessage head;
	private PendingMessage tail;
	
	public void pending(Object msg, ChannelPromise promise) {
		PendingMessage message = new PendingMessage(msg,promise);
		PendingMessage currentTail = tail;
		if (currentTail == null) {
			tail = head = message;
		} else {
			currentTail.next(message);
			tail = message;
		}
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

	public void forEach(Consumer<PendingMessage> consumer) {
		PendingMessage message = head;
		head = tail = null;
		while (message != null) {
			PendingMessage next = message.next();
			consumer.accept(message);
			message = next;
		}
	}

}
