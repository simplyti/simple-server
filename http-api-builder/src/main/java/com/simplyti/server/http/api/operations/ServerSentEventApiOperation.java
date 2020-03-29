package com.simplyti.server.http.api.operations;

import java.util.Map;

import com.simplyti.server.http.api.builder.sse.ServerSentEventAnyApiContextConsumer;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.sse.ServerSentEventAnyApiContext;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class ServerSentEventApiOperation extends ApiOperation<ServerSentEventAnyApiContext> {
	
	public ServerSentEventApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, ServerSentEventAnyApiContextConsumer consumer, ApiContextFactory contextFactory,
			boolean notFoundOnNull) {
		super(method, pattern,metadata, consumer,contextFactory, true, notFoundOnNull);
	}

}
