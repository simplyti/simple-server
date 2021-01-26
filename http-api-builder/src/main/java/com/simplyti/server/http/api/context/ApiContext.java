package com.simplyti.server.http.api.context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.simplyti.server.http.api.builder.sse.ServerSentEventApiContextConsumer;
import com.simplyti.server.http.api.builder.stream.ChunkedResponseContextConsumer;
import com.simplyti.server.http.api.builder.ws.WebSocketApiContextConsumer;
import com.simplyti.service.sync.VoidCallable;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.EventExecutor;

public interface ApiContext {
	
	String queryParam(String key);
	boolean queryParaAsBoolean(String key);
	Integer queryParamAsInt(String key);
	Integer queryParamAsInt(String key, int defaultValue);
	Long queryParamAsLong(String key);
	Long queryParamAsLong(String key, long defaultValue);
	
	List<String> queryParams(String key);
	Map<String,List<String>> queryParams();
	
	String pathParam(String name);
	Integer pathParamAsInt(String key);
	Long pathParamAsLong(String key);
	
	HttpRequest request();
	Channel channel();
	EventExecutor executor();
	
	Future<Void> failure(Throwable cause);
	
	Future<Void> close();
	
	Future<Void> sendChunked(ChunkedResponseContextConsumer object);
	Future<Void> webSocket(WebSocketApiContextConsumer ctx);
	Future<Void> serverSentEvent(ServerSentEventApiContextConsumer ctx);
	
	<T> Future<T> sync(Callable<T> task);
	Future<Void> sync(VoidCallable task);
	
}
