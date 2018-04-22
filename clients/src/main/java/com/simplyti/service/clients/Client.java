package com.simplyti.service.clients;

import com.simplyti.service.clients.channel.monitor.ClientMonitor;

public interface Client<B extends ClientRequestBuilder<B>> {
	
	public B withEndpoin(Endpoint endpoint);
	
	public ClientMonitor monitor();
	
}
