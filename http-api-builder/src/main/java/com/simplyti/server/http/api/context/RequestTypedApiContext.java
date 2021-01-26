package com.simplyti.server.http.api.context;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpObject;

public interface RequestTypedApiContext<T> extends WithBodyApiContext, ApiContext {

	T body();

	Future<Void> writeAndFlush(String message);
	Future<Void> writeAndFlush(ByteBuf body);
	Future<Void> writeAndFlush(HttpObject response);
	Future<Void> writeAndFlushEmpty();

	Future<Void> send(String message);
	Future<Void> send(ByteBuf body);
	Future<Void> send(HttpObject response);
	Future<Void> sendEmpty();

}
