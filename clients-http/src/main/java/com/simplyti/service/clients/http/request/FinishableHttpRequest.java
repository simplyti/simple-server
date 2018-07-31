package com.simplyti.service.clients.http.request;

import java.util.Map;
import java.util.function.Consumer;

import com.simplyti.service.clients.http.sse.ServerEvent;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Future;

public interface FinishableHttpRequest {

	Future<FullHttpResponse> fullResponse();

	Future<Void> forEach(Consumer<HttpObject> consumer);

	Future<Void> stream(Consumer<ByteBuf> consumer);
	
	Future<Void> sse(Consumer<ServerEvent> consumer);

	FinishableHttpRequest params(Map<String, String> params);

}
