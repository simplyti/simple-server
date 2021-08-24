package com.simplyti.service.clients.http.request;

import java.util.function.Function;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

public interface BaseFinishableHttpRequestBuilder<O> extends StatusCheckableRequestBuilder<O> {
	
	Future<FullHttpResponse> fullResponse();

	<T> Future<T> fullResponse(Function<FullHttpResponse,T> object);
	
	StreamedHandledHttpRequestBuilder<ByteBuf> stream();
	
	ServerSentEventRequestBuilder serverSentEvents();
	
}
