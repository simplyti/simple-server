package com.simplyti.server.http.api.builder.stream;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.StreamAnyApiOperation;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class StreamedRequestResponseTypableApiBuilderImpl implements StreamedRequestResponseTypableApiBuilder {

	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory streamAnyContextFactory;

	public StreamedRequestResponseTypableApiBuilderImpl(ApiContextFactory streamContextFactory, ApiOperations operations, HttpMethod method, String path) {
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.streamAnyContextFactory=streamContextFactory;
	}

	@Override
	public void then(StreamdRequestApiContextConsumer consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new StreamAnyApiOperation(method,apiPattern,null,consumer,streamAnyContextFactory));
	}

}
