package com.simplyti.service.api;

import java.util.concurrent.Callable;

import com.simplyti.service.sync.VoidCallable;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;

public interface APIContext<T> {
	
	public Future<Void> send(T response);
	public Future<Void> send(HttpObject response);
	public Future<Void> failure(Throwable error);
	
	public Future<T> sync(Callable<T> task);
	public Future<Void> sync(VoidCallable task);
	
	public Channel channel();
	public EventExecutor executor();
	public HttpRequest request();
	public Future<Void> close();

}
