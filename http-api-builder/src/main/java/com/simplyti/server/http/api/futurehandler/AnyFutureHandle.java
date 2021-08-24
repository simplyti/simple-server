package com.simplyti.server.http.api.futurehandler;

import java.util.function.Function;

import com.simplyti.server.http.api.builder.ApiContextConsumer;
import com.simplyti.server.http.api.context.AnyApiContext;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpObject;
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
				send(ctx, future.getNow());
			}else {
				ctx.failure(future.cause());
			}
		});
	}
	
	private void send(AnyApiContext ctx, T value) {
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
