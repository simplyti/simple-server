package com.simplyti.service.clients.init;

import com.simplyti.service.clients.ClientRequestChannel;

import io.netty.util.concurrent.Promise;

public interface ClientChannelPrimiseInitializer<T> {
	
	void initialize(ClientRequestChannel<T> channel, Promise<T> promise);

}
