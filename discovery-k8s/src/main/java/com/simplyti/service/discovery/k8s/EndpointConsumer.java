package com.simplyti.service.discovery.k8s;

import com.simplyti.service.clients.endpoint.Endpoint;

@FunctionalInterface
public interface EndpointConsumer {
	
	void consume(Endpoint endpoint, String host, String path);

}
