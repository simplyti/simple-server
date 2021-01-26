package com.simplyti.server.http.api.context.chunked;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;

public interface ChunkedResponseContext {

	Future<Void> send(String data);

	Future<Void> send(ByteBuf data);

	Future<Void> finish();


}
