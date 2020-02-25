package com.simplyti.server.http.api.context;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;

public interface RequestTypedApiContext<T> extends ApiContext {

	T body();

	Future<Void> writeAndFlush(String message);
	Future<Void> writeAndFlush(ByteBuf body);

	Future<Void> send(String message);
	Future<Void> send(ByteBuf body);

}
