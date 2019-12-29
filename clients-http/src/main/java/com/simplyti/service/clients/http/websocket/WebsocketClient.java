package com.simplyti.service.clients.http.websocket;

import com.simplyti.service.clients.stream.InputDataStream;
import com.simplyti.util.concurrent.Future;

public interface WebsocketClient extends InputDataStream {

	Future<Void> send(String data);

}
