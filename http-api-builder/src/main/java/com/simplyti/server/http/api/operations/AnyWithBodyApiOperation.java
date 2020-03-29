package com.simplyti.server.http.api.operations;

import java.util.Map;

import com.simplyti.server.http.api.builder.ApiWithBodyContextConsumer;
import com.simplyti.server.http.api.context.AnyWithBodyApiContext;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class AnyWithBodyApiOperation extends ApiOperation<AnyWithBodyApiContext> {
	
	public AnyWithBodyApiOperation(HttpMethod method, ApiPattern pattern,Map<String,Object> metadata, ApiWithBodyContextConsumer consumer, ApiContextFactory contextFactory,
			boolean notFoundOnNull, int maxBodyLength) {
		super(method, pattern,metadata,consumer,contextFactory,false, notFoundOnNull, maxBodyLength);
	}

}
