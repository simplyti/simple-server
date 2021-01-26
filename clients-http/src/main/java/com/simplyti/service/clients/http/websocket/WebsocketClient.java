package com.simplyti.service.clients.http.websocket;

import java.util.function.Consumer;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;

public interface WebsocketClient {

	Future<Void> send(String data);

	WebsocketClient onMessage(Consumer<ByteBuf> consumer);

	Future<Void> closeFuture();

	Future<Void> connectFuture();

}
