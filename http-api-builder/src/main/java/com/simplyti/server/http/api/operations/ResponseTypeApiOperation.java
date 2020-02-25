package com.simplyti.server.http.api.operations;

import java.util.Map;

import com.simplyti.server.http.api.builder.ResponseTypedApiContextConsumer;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.ResponseTypedApiContext;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class ResponseTypeApiOperation<T> extends ApiOperation<ResponseTypedApiContext<T>> {

	public ResponseTypeApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, ResponseTypedApiContextConsumer<T> consumer,
			ApiContextFactory contextFactory) {
		super(method, pattern, metadata, consumer, contextFactory, false);
	}
	
}
