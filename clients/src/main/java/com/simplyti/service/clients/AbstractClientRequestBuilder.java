package com.simplyti.service.clients;

import com.simplyti.service.clients.trace.RequestTracer;

public abstract class AbstractClientRequestBuilder<B extends ClientRequestBuilder<B>> implements ClientRequestBuilder<B>{
	
	private static final int DEFAULT_READ_TIMEOUT = 5000;
	private int readTimeout = DEFAULT_READ_TIMEOUT;
	
	private RequestTracer<?,?> tracer;
	private Endpoint endpoint;
	
	public AbstractClientRequestBuilder(Endpoint endpoint) {
		this.endpoint=endpoint;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public B withEndpoint(Endpoint endpoint) {
		this.endpoint=endpoint;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public B withReadTimeout(int timeout) {
		this.readTimeout=timeout;
		return (B) this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public B withTracer(RequestTracer<?,?> tracer) {
		this.tracer=tracer;
		return (B) this;
	}
	
	protected ClientConfig config() {
		return new ClientConfig(endpoint,readTimeout,tracer);
	}

}
