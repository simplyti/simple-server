package com.simplyti.service.client;

import io.netty.channel.ChannelException;

public class ClosedChannelException extends ChannelException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6758482887040705458L;
	
	public ClosedChannelException() {
		super("Channel closed");
	}

}
