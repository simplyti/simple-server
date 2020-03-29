package com.simplyti.server.http.api.builder;

import java.util.function.Function;

import com.simplyti.server.http.api.context.ResponseTypedApiContext;

import io.netty.util.concurrent.Future;

public interface ResponseBodyTypedApiBuilder<T> {
	
	ResponseBodyTypedApiBuilder<T> withNotFoundOnNull();

	void then(ResponseTypedApiContextConsumer<T> consumer);

	void thenFuture(Function<ResponseTypedApiContext<T>,Future<T>> object);

}
