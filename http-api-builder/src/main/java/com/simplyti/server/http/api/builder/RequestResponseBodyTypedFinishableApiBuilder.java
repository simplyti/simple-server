package com.simplyti.server.http.api.builder;

import java.util.function.Function;

import com.simplyti.server.http.api.context.RequestResponseTypedApiContext;

import io.netty.util.concurrent.Future;

public interface RequestResponseBodyTypedFinishableApiBuilder<T, U> {

	void then(RequestResponseTypedApiContextConsumer<T,U> consumer);

	void thenFuture(Function<RequestResponseTypedApiContext<T,U>,Future<U>> object);

}
