package com.simplyti.service.channel.handler;

import io.netty.channel.SimpleChannelInboundHandler;

public abstract class DefaultBackendRequestHandler extends SimpleChannelInboundHandler<Object> {
	
	public DefaultBackendRequestHandler() {
		this(true);
	}
	
	public DefaultBackendRequestHandler(boolean autoRelease) {
		super(autoRelease);
	}
	
}
