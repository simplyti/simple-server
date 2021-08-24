package com.simplyti.service.transport;

import com.simplyti.util.concurrent.Future;

import io.netty.channel.EventLoop;
import io.netty.util.AttributeKey;

public interface ServerTransport {
	
	public static AttributeKey<Listener> LISTENER = AttributeKey.valueOf("channel.listener");

	Future<Void> start(EventLoop eventLoop);

	Future<Void> stop(EventLoop eventLoop);

}
