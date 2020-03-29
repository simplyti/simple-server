package com.simplyti.server.http.api.operations;

import java.util.Map;

import com.simplyti.server.http.api.builder.ApiContextConsumer;
import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class AnyApiOperation extends ApiOperation<AnyApiContext> {

	public AnyApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, ApiContextConsumer consumer, ApiContextFactory contextFactory,
			boolean notFoundOnNull) {
		super(method, pattern, metadata, consumer,contextFactory,false, notFoundOnNull);
	}

}
