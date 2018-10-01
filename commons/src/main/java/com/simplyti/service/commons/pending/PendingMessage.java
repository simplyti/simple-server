package com.simplyti.service.commons.pending;

import io.netty.util.Recycler;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
public class PendingMessage {
	
	private static final Recycler<PendingMessage> RECYCLER = new Recycler<PendingMessage>() {
		@Override
		protected PendingMessage newObject(Handle<PendingMessage> handle) {
			return new PendingMessage(handle);
		}
	};

	private final Recycler.Handle<PendingMessage> handle;
	
	@Getter
	private Promise<Void> promise;
	
	@Getter
	private Object msg;
	
	@Setter @Getter
	private PendingMessage next;

	private PendingMessage(Recycler.Handle<PendingMessage> handle) {
		this.handle = handle;
	}

	static PendingMessage newInstance(Promise<Void> promise, Object msg) {
		PendingMessage message = RECYCLER.get();
		message.promise = promise;
		message.msg = msg;
		return message;
	}

	public void recycle() {
		promise = null;
		msg = null;
		next = null;
		handle.recycle(this);
	}
}
