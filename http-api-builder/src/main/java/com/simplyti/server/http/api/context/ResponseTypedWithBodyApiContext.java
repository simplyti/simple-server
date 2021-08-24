package com.simplyti.server.http.api.context;

import io.netty.buffer.ByteBuf;

public interface ResponseTypedWithBodyApiContext<T> extends ResponseTypedApiContext<T>, WithBodyApiContext {

	ByteBuf body();

}
