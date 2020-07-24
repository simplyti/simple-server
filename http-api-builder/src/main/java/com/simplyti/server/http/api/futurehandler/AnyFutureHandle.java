package com.simplyti.server.http.api.futurehandler;

import java.util.function.Function;

import com.simplyti.server.http.api.builder.ApiContextConsumer;
import com.simplyti.server.http.api.context.AnyApiContext;

import io.netty.util.concurrent.Future;

public class AnyFutureHandle<T> implements ApiContextConsumer {
	
	private final Function<AnyApiContext, Future<T>> futureSupplier;

	public AnyFutureHandle(Function<AnyApiContext, Future<T>> futureSupplier) {
		this.futureSupplier=futureSupplier;
	}

	@Override
	public void accept(AnyApiContext ctx) {
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
