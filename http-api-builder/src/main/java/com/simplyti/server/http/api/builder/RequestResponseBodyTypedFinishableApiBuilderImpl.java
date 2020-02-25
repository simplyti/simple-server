package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.RequestResponseTypeApiOperation;
import com.simplyti.server.http.api.pattern.ApiPattern;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class RequestResponseBodyTypedFinishableApiBuilderImpl<T,U> implements RequestResponseBodyTypedFinishableApiBuilder<T, U> {

	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory requestTypedContextFactory;
	private final TypeLiteral<T> requestType;

	public RequestResponseBodyTypedFinishableApiBuilderImpl(ApiContextFactory requestTypedContextFactory,ApiOperations operations, HttpMethod method, String path, TypeLiteral<T> requestType) {
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.requestTypedContextFactory=requestTypedContextFactory;
		this.requestType=requestType;
	}
	
	@Override
	public void then(RequestResponseTypedApiContextConsumer<T, U> consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new RequestResponseTypeApiOperation<>(method,apiPattern,null,requestType,consumer,requestTypedContextFactory));
	}

}
