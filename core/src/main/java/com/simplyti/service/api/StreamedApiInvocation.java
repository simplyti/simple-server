package com.simplyti.service.api;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

public class StreamedApiInvocation implements ApiInvocation{
	
	private final HttpRequest request;
	private final ApiOperation<?, ?, ?> operation;
	
	public StreamedApiInvocation(HttpRequest request, ApiOperation<?, ?, ?> operation) {
		this.request=request;
		this.operation=operation;
	}

	@Override
	public HttpRequest request() {
		return request;
	}
	
	@Override
	public ApiOperation<?, ?, ?> operation() {
		return operation;
	}

	@Override
	public HttpHeaders headers() {
		return request.headers();
	}

}
