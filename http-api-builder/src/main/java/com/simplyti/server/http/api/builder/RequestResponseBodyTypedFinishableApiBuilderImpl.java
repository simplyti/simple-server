package com.simplyti.server.http.api.builder;

import java.util.Map;
import java.util.function.Function;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.RequestResponseTypedApiContext;
import com.simplyti.server.http.api.futurehandler.RequestResponseBodyTypedFutureHandle;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.RequestResponseTypeApiOperation;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.matcher.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.concurrent.Future;

public class RequestResponseBodyTypedFinishableApiBuilderImpl<T,U> implements RequestResponseBodyTypedFinishableApiBuilder<T, U> {

	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory requestTypedContextFactory;
	private final TypeLiteral<T> requestType;
	private final Map<String, Object> metadata;
	
	private boolean notFoundOnNull;

	public RequestResponseBodyTypedFinishableApiBuilderImpl(ApiContextFactory requestTypedContextFactory,ApiOperations operations, HttpMethod method, String path, Map<String,Object> metadata, 
			boolean notFoundOnNull, TypeLiteral<T> requestType) {
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.requestTypedContextFactory=requestTypedContextFactory;
		this.requestType=requestType;
		this.metadata=metadata;
		this.notFoundOnNull=notFoundOnNull;
	}
	
	@Override
	public void then(RequestResponseTypedApiContextConsumer<T, U> consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new RequestResponseTypeApiOperation<>(method,apiPattern,metadata,requestType,consumer,requestTypedContextFactory, notFoundOnNull));
	}

	@Override
	public void thenFuture(Function<RequestResponseTypedApiContext<T, U>, Future<U>> futureSupplier) {
		then(new RequestResponseBodyTypedFutureHandle<T,U>(futureSupplier));
	}
	
	@Override
	public RequestResponseBodyTypedFinishableApiBuilder<T, U> withNotFoundOnNull() {
		this.notFoundOnNull=true;
		return this;
	}

}
