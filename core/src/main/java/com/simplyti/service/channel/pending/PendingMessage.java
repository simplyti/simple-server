package com.simplyti.service.channel.pending;

import io.netty.channel.ChannelPromise;

public class PendingMessage {

	private final Object object;
	private final ChannelPromise promise;
	
	private PendingMessage next;
	
	public PendingMessage(Object object,ChannelPromise promise) {
		this.object=object;
		this.promise=promise;
	}

	public void next(PendingMessage next) {
		this.next=next;
	}

	public PendingMessage next() {
		return next;
	}

	public Object msg() {
		return object;
	}

	public ChannelPromise promise() {
		return promise;
	}

}
