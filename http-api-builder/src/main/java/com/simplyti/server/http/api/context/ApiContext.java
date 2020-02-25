package com.simplyti.server.http.api.context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.simplyti.util.concurrent.Future;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.EventExecutor;

public interface ApiContext {
	
	String queryParam(String name);
	Iterable<String> queryParams(String name);
	Map<String,List<String>> queryParams();
	
	String pathParam(String name);
	
	HttpRequest request();
	Channel channel();
	
	EventExecutor executor();
	
	Future<Void> failure(Throwable cause);
	
	Future<Void> close();
	
	<T> Future<T> sync(Callable<T> task);
	
}
