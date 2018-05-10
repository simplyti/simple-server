package com.simplyti.service.api;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;

public interface APIContext<T> {
	
	public Future<Void> send(T response);
	public Future<Void> send(FullHttpResponse response);
	public Future<Void> failure(Throwable error);
	
	public Channel channel();
	public EventExecutor executor();
	public HttpRequest request();
	public Future<Void> close();

}
