package com.simplyti.server.http.api.futurehandler;

import java.util.function.Function;

import com.simplyti.server.http.api.builder.RequestResponseTypedApiContextConsumer;
import com.simplyti.server.http.api.context.RequestResponseTypedApiContext;

import io.netty.util.concurrent.Future;

public class RequestResponseBodyTypedFutureHandle<T, U> implements RequestResponseTypedApiContextConsumer<T, U> {

	private final Function<RequestResponseTypedApiContext<T, U>, Future<U>> futureSupplier;

	public RequestResponseBodyTypedFutureHandle(Function<RequestResponseTypedApiContext<T, U>, Future<U>> futureSupplier) {
		this.futureSupplier=futureSupplier;
	}

	@Override
	public void accept(RequestResponseTypedApiContext<T, U> ctx) {
		Future<U> future = futureSupplier.apply(ctx);
		future.addListener(f->{
			if(f.isSuccess()) {
				ctx.send(future.getNow());
			}else {
				ctx.failure(future.cause());
			}
		});
	}

}
