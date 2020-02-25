package com.simplyti.server.http.api.context;

import io.netty.buffer.ByteBuf;

public interface AnyWithBodyApiContext extends AnyApiContext {
	
	ByteBuf body();

}
