package com.simplyti.service.clients.stream;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.CloseableClannel;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;

public interface InputDataStream extends CloseableClannel {

	Future<Void> onData(Consumer<ByteBuf> consumer);
	
}
