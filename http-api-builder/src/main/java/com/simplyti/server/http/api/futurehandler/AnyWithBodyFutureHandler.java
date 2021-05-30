package com.simplyti.server.http.api.futurehandler;

import java.util.function.Function;

import com.simplyti.server.http.api.builder.ApiWithBodyContextConsumer;
import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.server.http.api.context.AnyWithBodyApiContext;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Future;

public class AnyWithBodyFutureHandler<T> implements ApiWithBodyContextConsumer {

	private final Function<AnyWithBodyApiContext, Future<T>> futureSupplier;

	public AnyWithBodyFutureHandler(Function<AnyWithBodyApiContext, Future<T>> futureSupplier) {
		this.futureSupplier=futureSupplier;
	}

	@Override
	public void accept(AnyWithBodyApiContext ctx) {
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
