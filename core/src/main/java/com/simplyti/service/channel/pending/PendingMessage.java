package com.simplyti.service.channel.pending;

public class PendingMessage {

	private final Object object;
	
	private PendingMessage msg;

	public PendingMessage(Object object) {
		this.object=object;
	}

	public void next(PendingMessage msg) {
		this.msg=msg;
	}

	public PendingMessage next() {
		return msg;
	}

	public Object msg() {
		return object;
	}

}
