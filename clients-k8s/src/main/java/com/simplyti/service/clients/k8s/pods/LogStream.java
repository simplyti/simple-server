package com.simplyti.service.clients.k8s.pods;

import java.util.function.Consumer;

import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;

public interface LogStream {

	Future<Void> follow(Consumer<ByteBuf> object);

	void close();

}
