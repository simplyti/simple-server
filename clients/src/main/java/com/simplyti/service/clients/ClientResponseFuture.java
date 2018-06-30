package com.simplyti.service.clients;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public class ClientResponseFuture<T> {

	private final Future<T> promise;
	private final Future<Channel> channelFuture;
	private final EventLoop eventLoop;

	public ClientResponseFuture(EventLoop eventLoop, Future<Channel> channelFuture, Future<T> promise) {
		this.eventLoop=eventLoop;
		this.promise=promise;
		this.channelFuture=channelFuture;
	}

	public Future<T> future() {
		return promise;
	}

	public Future<Channel> channelFuture() {
		return channelFuture;
	}

	public EventLoop eventLoop() {
		return eventLoop;
	}

}
