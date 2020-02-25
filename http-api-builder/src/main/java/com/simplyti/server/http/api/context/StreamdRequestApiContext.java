package com.simplyti.server.http.api.context;

import java.util.function.Consumer;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;

public interface StreamdRequestApiContext extends AnyApiContext {

	Future<Void> stream(Consumer<ByteBuf> consumer);

}
