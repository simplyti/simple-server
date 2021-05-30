package com.simplyti.server.http.api.context.chunked;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.EventExecutor;

public interface ChunkedResponseContext {

	Future<Void> send(String data);

	Future<Void> send(ByteBuf data);

	Future<Void> finish();

	EventExecutor executor();


}
