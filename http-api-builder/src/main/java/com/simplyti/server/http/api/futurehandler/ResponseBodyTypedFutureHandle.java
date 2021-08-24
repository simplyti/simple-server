package com.simplyti.server.http.api.futurehandler;

import java.util.function.Function;

import com.simplyti.server.http.api.builder.ResponseTypedApiContextConsumer;
import com.simplyti.server.http.api.context.ResponseTypedApiContext;

import io.netty.util.concurrent.Future;

public class ResponseBodyTypedFutureHandle<T> implements ResponseTypedApiContextConsumer<T> {

	private final Function<ResponseTypedApiContext<T>, Future<T>> futureSupplier;

	public ResponseBodyTypedFutureHandle(Function<ResponseTypedApiContext<T>, Future<T>> futureSupplier) {
		this.futureSupplier=futureSupplier;
	}

	@Override
	public void accept(ResponseTypedApiContext<T> ctx) {
		Future<T> future = futureSupplier.apply(ctx);
		future.addListener(f->{
			if(f.isSuccess()) {
				ctx.send(future.getNow());
			}else {
				ctx.failure(future.cause());
			}
		});
	}

}
