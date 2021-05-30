package com.simplyti.server.http.api.futurehandler;

import java.util.function.Function;

import com.simplyti.server.http.api.builder.ResponseTypedWithRequestApiContextConsumer;
import com.simplyti.server.http.api.context.ResponseTypedWithBodyApiContext;

import io.netty.util.concurrent.Future;

public class ResponseTypedWithBodyFutureHandle<T> implements ResponseTypedWithRequestApiContextConsumer<T> {

	private final Function<ResponseTypedWithBodyApiContext<T>, Future<T>> futureSupplier;

	public ResponseTypedWithBodyFutureHandle(Function<ResponseTypedWithBodyApiContext<T>, Future<T>> futureSupplier) {
		this.futureSupplier=futureSupplier;
	}
	
	@Override
	public void accept(ResponseTypedWithBodyApiContext<T> ctx) {
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
