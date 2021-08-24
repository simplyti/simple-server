package com.simplyti.server.http.api.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.ResponseTypedApiContext;
import com.simplyti.server.http.api.futurehandler.ResponseBodyTypedFutureHandle;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ResponseTypeApiOperation;
import com.simplyti.service.matcher.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.concurrent.Future;

public class ResponseBodyTypedApiBuilderImpl<T> implements ResponseBodyTypedApiBuilder<T> {
	
	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory factory;
	
	private Map<String, Object> metadata;
	private boolean notFoundOnNull;
	private int maxBodyLength;

	public ResponseBodyTypedApiBuilderImpl(ApiContextFactory factory, ApiOperations operations, HttpMethod method, String path,
			Map<String,Object> metadata, boolean notFoundOnNull) {
		this.factory=factory;
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.metadata=metadata;
		this.notFoundOnNull=notFoundOnNull;
	}

	@Override
	public void then(ResponseTypedApiContextConsumer<T> consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new ResponseTypeApiOperation<>(method,apiPattern,metadata,consumer,factory, notFoundOnNull, maxBodyLength));
	}

	@Override
	public void thenFuture(Function<ResponseTypedApiContext<T>, Future<T>> futureSupplier) {
		then(new ResponseBodyTypedFutureHandle<T>(futureSupplier));
	}

	@Override
	public ResponseBodyTypedApiBuilder<T> withNotFoundOnNull() {
		this.notFoundOnNull=true;
		return this;
	}

	@Override
	public ResponseBodyTypedApiBuilder<T> withMeta(String key, String value) {
		if(metadata==null) {
			metadata = new HashMap<>();
		}
		metadata.put(key, value);
		return this;
	}

}
