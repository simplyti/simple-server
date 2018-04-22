package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Future;

public interface FinishableHttpRequest {

	Future<FullHttpResponse> fullResponse();

	Future<Void> forEach(Consumer<HttpObject> consumer);

	Future<Void> stream(Consumer<ByteBuf> consumer);

}
