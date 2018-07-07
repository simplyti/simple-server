package com.simplyti.service.clients.http.ws;

import io.netty.util.concurrent.Future;

public interface WebSocketClient {

	Future<Void> send(String string);

}
