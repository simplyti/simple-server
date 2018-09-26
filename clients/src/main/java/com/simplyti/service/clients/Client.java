package com.simplyti.service.clients;

import com.simplyti.service.clients.channel.monitor.ClientMonitor;

import io.netty.util.concurrent.Future;

public interface Client<B extends ClientRequestBuilder<B>> {
	
	public B withEndpoin(Endpoint endpoint);
	
	public ClientMonitor monitor();
	
	public Future<Void> close();
	
}
