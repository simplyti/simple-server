package com.simplyti.server.http.api.operations;

import java.util.Map;

import com.simplyti.server.http.api.builder.ResponseTypedWithRequestApiContextConsumer;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.ResponseTypedWithBodyApiContext;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class ResponseTypeWithBodyApiOperation<T> extends ApiOperation<ResponseTypedWithBodyApiContext<T>> {

	public ResponseTypeWithBodyApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, ResponseTypedWithRequestApiContextConsumer<T> consumer, 
			ApiContextFactory contextFactory) {
		super(method, pattern, metadata, consumer, contextFactory, false);
	}
	
}