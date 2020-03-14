package com.simplyti.server.http.api.builder.sse;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ServerSentEventApiOperation;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class ServerSentEventApiBuilderImpl implements ServerSentEventApiBuilder {
	
	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory streamAnyContextFactory;

	public ServerSentEventApiBuilderImpl(ApiContextFactory streamContextFactory, ApiOperations operations, HttpMethod method, String path) {
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.streamAnyContextFactory=streamContextFactory;
	}

	@Override
	public void then(ServerSentEventAnyApiContextConsumer consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new ServerSentEventApiOperation(method,apiPattern,null,consumer,streamAnyContextFactory));
	}

}
