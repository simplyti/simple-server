package com.simplyti.service.clients.stream;

import java.util.function.Supplier;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.util.concurrent.Future;

public interface PendingRequest {
	
	Future<ClientChannel> channel();

	<U> Future<U> addHandlerAndSend(Future<ClientChannel> futureChannel,  Supplier<io.netty.util.concurrent.Future<U>> requestHandlerInit);

}
