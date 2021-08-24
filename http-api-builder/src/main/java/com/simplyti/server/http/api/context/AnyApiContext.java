package com.simplyti.server.http.api.context;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpObject;

public interface AnyApiContext extends ApiContext {
	
	Future<Void> writeAndFlushEmpty();
	Future<Void> writeAndFlush(String message);
	Future<Void> writeAndFlush(ByteBuf body);
	Future<Void> writeAndFlush(HttpObject response);
	Future<Void> writeAndFlush(Object value);
	
	Future<Void> sendEmpty();
	Future<Void> send(String string);
	Future<Void> send(ByteBuf body);
	Future<Void> send(HttpObject resp);
	Future<Void> send(Object value);
	
}
