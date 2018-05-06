package com.simplyti.service.api;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;

public interface ApiInvocationContext<I,O> extends APIContext<O>{
	
	public I body();
	public List<String> queryParams(String name);
	public String queryParam(String name);
	public String pathParam(String key);
	public String uri();
	public HttpHeaders headers();
	
	public Future<Void> send(FullHttpResponse response);
	public Future<Void> close();
	public EventExecutor executor();
	
	public ChannelHandlerContext channelContext();
	public HttpRequest request();
	
}
