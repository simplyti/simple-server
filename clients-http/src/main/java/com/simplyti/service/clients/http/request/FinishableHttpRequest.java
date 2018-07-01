package com.simplyti.service.clients.http.request;

import java.util.Map;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;

public interface FinishableHttpRequest {

	Future<FullHttpResponse> fullResponse();

	Future<Void> forEach(Consumer<Object> consumer);

	Future<Void> stream(Consumer<ByteBuf> consumer);

	FinishableHttpRequest params(Map<String, String> params);

}
