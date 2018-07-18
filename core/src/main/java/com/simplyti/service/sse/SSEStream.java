package com.simplyti.service.sse;

import io.netty.util.concurrent.Future;

public interface SSEStream {

	Future<Void> send(String event);

}
