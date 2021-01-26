package com.simplyti.server.http.api.context.ws;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;

public interface WebSocketApiContext {

	Future<Void> send(String data);
	Future<Void> send(ByteBuf data);

	void onMessage(StringConsumer consumer);

	void onMessage(ByteBufConsumer consumer);
	
	Future<Void> close();
	
}
