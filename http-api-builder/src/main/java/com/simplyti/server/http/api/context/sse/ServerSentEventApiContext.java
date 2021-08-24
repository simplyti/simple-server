package com.simplyti.server.http.api.context.sse;

import com.simplyti.util.concurrent.Future;

import io.netty.util.concurrent.EventExecutor;

public interface ServerSentEventApiContext {

	Future<Void> send(String string);
	
	EventExecutor executor();

}
