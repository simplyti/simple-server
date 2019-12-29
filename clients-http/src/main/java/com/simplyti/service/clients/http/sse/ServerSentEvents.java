package com.simplyti.service.clients.http.sse;

import java.util.function.Consumer;

import com.simplyti.util.concurrent.Future;

public interface ServerSentEvents {

	Future<Void> onEvent(Consumer<ServerEvent> consumer);

	void close();
	
}
