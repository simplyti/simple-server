package com.simplyti.service.transport;

import com.simplyti.util.concurrent.Future;

import io.netty.channel.EventLoop;

public interface ServerTransport {

	Future<Void> start(EventLoop eventLoop);

	Future<Void> stop(EventLoop eventLoop);

}
