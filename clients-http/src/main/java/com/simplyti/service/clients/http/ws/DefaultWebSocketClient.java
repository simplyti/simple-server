package com.simplyti.service.clients.http.ws;

import com.simplyti.service.clients.ClientRequestChannel;
import com.simplyti.service.clients.stream.StreamedClient;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.Future;

public class DefaultWebSocketClient extends StreamedClient<WebSocketFrame> implements WebSocketClient {
	
	public DefaultWebSocketClient(Future<ClientRequestChannel<Void>> clientChannel, EventLoop executor) {
		super(clientChannel, executor);
	}

	public Future<Void> send(String msg) {
		return send(new TextWebSocketFrame(msg));
	}

}
