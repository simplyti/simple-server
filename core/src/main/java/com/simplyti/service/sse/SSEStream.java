package com.simplyti.service.sse;

import io.netty.util.concurrent.Future;

public interface SSEStream {

	Future<Void> send(String data);
	
	Future<Void> send(String event, String data);
	
	Future<Void> send(String id, String event, String data);

}
