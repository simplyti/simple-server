package com.simplyti.service.api.builder;

import java.util.function.Consumer;
import java.util.function.Function;

import com.simplyti.service.api.ApiInvocationContext;

import io.netty.util.concurrent.Future;

public class InvocationFutureHandle<I, O> implements Consumer<com.simplyti.service.api.ApiInvocationContext<I, O>> {

	private final Function<ApiInvocationContext<I, O>, Future<O>> futureFunction;

	public InvocationFutureHandle(Function<ApiInvocationContext<I, O>, Future<O>> futureFunction) {
		this.futureFunction=futureFunction;
	}

	@Override
	public void accept(ApiInvocationContext<I, O> ctx) {
		Future<O> future = futureFunction.apply(ctx);
		future.addListener(f->{
			if(f.isSuccess()) {
				ctx.send(future.getNow());
			}else {
				ctx.failure(future.cause());
			}
		});
	}

}
