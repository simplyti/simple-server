package com.simplyti.service.clients.request;

import com.simplyti.service.clients.endpoint.Endpoint;

public interface BaseClientRequestBuilder<T> {

	T withEndpoint(String host, int port);
	T withEndpoint(Endpoint endpoint);
	
	T withReadTimeout(long timeoutMillis);
	T withResponseTimeout(long timeoutMillis);
	
}
