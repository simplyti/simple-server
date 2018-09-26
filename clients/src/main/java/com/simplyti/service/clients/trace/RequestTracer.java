package com.simplyti.service.clients.trace;


public interface RequestTracer<I,O> {

	void request(String requestId,I msg);
	void response(String requestId,O msg, long duration);

}
