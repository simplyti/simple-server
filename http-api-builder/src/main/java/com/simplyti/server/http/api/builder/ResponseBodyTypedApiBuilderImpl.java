package com.simplyti.server.http.api.builder;

import java.util.function.Function;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.ResponseTypedApiContext;
import com.simplyti.server.http.api.futurehandler.ResponseBodyTypedFutureHandle;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ResponseTypeApiOperation;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.concurrent.Future;

public class ResponseBodyTypedApiBuilderImpl<T> implements ResponseBodyTypedApiBuilder<T> {
	
	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory factory;

	public ResponseBodyTypedApiBuilderImpl(ApiContextFactory factory, ApiOperations operations, HttpMethod method, String path) {
		this.factory=factory;
		this.operations=operations;
		this.method=method;
		this.path=path;
	}

	@Override
	public void then(ResponseTypedApiContextConsumer<T> consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new ResponseTypeApiOperation<>(method,apiPattern,null,consumer,factory));
	}

	@Override
	public void thenFuture(Function<ResponseTypedApiContext<T>, Future<T>> futureSupplier) {
		then(new ResponseBodyTypedFutureHandle<T>(futureSupplier));
	}

}
