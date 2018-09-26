package com.simplyti.service.client.tracer;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.trace.RequestTracer;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class SimpleRequestTracer implements RequestTracer<FullHttpRequest,FullHttpResponse>{

	private final List<FullHttpRequest> requests = new ArrayList<>();

	@Override
	public void request(String id, FullHttpRequest msg) {
		this.requests.add(msg);
	}
	
	@Override
	public void response(String id, FullHttpResponse msg, long duration) {
	}

	public List<FullHttpRequest> requests() {
		return requests;
	}

}
