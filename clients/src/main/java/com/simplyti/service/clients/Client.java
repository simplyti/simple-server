package com.simplyti.service.clients;

import com.simplyti.service.clients.monitor.ClientMonitor;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;

public interface Client<R> {

	static GenericClientBuilder builder() {
		return new DefaultGenericClientBuilder();
	}

	R request();
	
	Future<Void> close();
	
	ClientMonitor monitor();

	EventLoopGroup eventLoopGroup();

}
