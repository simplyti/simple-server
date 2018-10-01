package com.simplyti.service.clients;

import com.simplyti.service.clients.trace.RequestTracer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent=true)
public class ClientConfig {
	
	private final Endpoint endpoint;
	private final long timeoutMillis;
	private final  RequestTracer<?,?> tracer;

}
