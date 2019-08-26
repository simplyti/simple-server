package com.simplyti.service;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public interface Service<T extends Service<T>> {
	
	public EventLoop executor();
	
	public Future<Void> stop();
	
	public Future<T> start();
	
	public Future<Void> stopFuture();
	
}
