package com.simplyti.service.api.builder;

import java.util.function.Consumer;
import java.util.function.Function;

import com.simplyti.service.api.ApiInvocationContext;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;

public class InvocationHttpFutureHandle<I,O> implements Consumer<ApiInvocationContext<I, O>> {

	private final Function<ApiInvocationContext<I, FullHttpResponse>, Future<FullHttpResponse>> futureFunction;

	public InvocationHttpFutureHandle(Function<ApiInvocationContext<I, FullHttpResponse>, Future<FullHttpResponse>> futureFunction) {
		this.futureFunction=futureFunction;
	}

	@Override
	public void accept(ApiInvocationContext<I, O> ctx) {
		@SuppressWarnings("unchecked")
		Future<FullHttpResponse> future = futureFunction.apply((ApiInvocationContext<I, FullHttpResponse>) ctx);
		future.addListener(f->{
			if(f.isSuccess()) {
				ctx.send(future.getNow());
			}else {
				ctx.failure(future.cause());
			}
		});
	}

}

