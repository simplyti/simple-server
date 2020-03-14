package com.simplyti.server.http.api.operations;

import java.util.Map;

import com.simplyti.server.http.api.builder.stream.StreamdRequestApiContextConsumer;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.stream.StreamdRequestApiContext;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class StreamAnyApiOperation extends ApiOperation<StreamdRequestApiContext> {
	
	public StreamAnyApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, StreamdRequestApiContextConsumer consumer, ApiContextFactory contextFactory) {
		super(method, pattern,metadata, consumer,contextFactory, true);
	}

}
