package com.simplyti.server.http.api.futurehandler;

import java.util.function.Function;

import com.simplyti.server.http.api.builder.RequestTypedApiContextConsumer;
import com.simplyti.server.http.api.context.RequestTypedApiContext;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Future;

public class RequestBodyTypedFutureHandle<T, U> implements RequestTypedApiContextConsumer<T> {

	private final Function<RequestTypedApiContext<T>, Future<U>> futureSupplier;

	public RequestBodyTypedFutureHandle(Function<RequestTypedApiContext<T>, Future<U>> futureSupplier) {
		this.futureSupplier=futureSupplier;
	}

	@Override
	public void accept(RequestTypedApiContext<T> ctx) {
		Future<U> future = futureSupplier.apply(ctx);
		future.addListener(f->{
			if(f.isSuccess()) {
				send(ctx,future.getNow());
			}else {
				ctx.failure(future.cause());
			}
		});
	}

	private void send(RequestTypedApiContext<T> ctx, U value) {
		if(value instanceof HttpObject) {
			ctx.send((HttpObject)value);
		} else if(value instanceof String) {
			ctx.send((String) value);
		} else if(value instanceof ByteBuf) {
			ctx.send((ByteBuf) value);
		} else {
			ctx.send(value);
		}
	}

}
