package com.simplyti.service.gateway.balancer;

import java.util.Collection;

import com.simplyti.service.clients.Endpoint;

public interface ServiceBalancer {

	Endpoint next();

	ServiceBalancer add(Endpoint endpoint);
	
	ServiceBalancer delete(Endpoint endpoint);
	
	ServiceBalancer clear();
	
	Collection<Endpoint> endpoints();

}
