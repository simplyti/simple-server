package com.simplyti.service.clients;

import com.simplyti.service.clients.trace.RequestTracer;

public interface ClientRequestBuilder<B extends ClientRequestBuilder<B>> {
	
	public B withReadTimeout(int i);
	public B withTracer(RequestTracer<?,?> tracer);

}
