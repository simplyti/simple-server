package com.simplyti.service.channel.pending;

import java.util.function.Consumer;

import io.netty.util.ReferenceCountUtil;

public class PendingMessages {
	
	private PendingMessage head;
	private PendingMessage tail;

	public void pending(Object msg) {
		PendingMessage message = new PendingMessage(msg);
		PendingMessage currentTail = tail;
		if (currentTail == null) {
			tail = head = message;
		} else {
			currentTail.next(message);
			tail = message;
		}
	}

	public void release() {
		PendingMessage write = head;
		head = tail = null;
		while (write != null) {
			PendingMessage next = write.next();
			ReferenceCountUtil.release(write.msg());
			write = next;
		}
	}

	public void forEach(Consumer<Object> consumer) {
		PendingMessage write = head;
		head = tail = null;
		while (write != null) {
			PendingMessage next = write.next();
			Object msg = write.msg();
			consumer.accept(msg);
			write = next;
		}
	}

}
