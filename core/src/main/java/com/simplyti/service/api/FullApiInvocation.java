package com.simplyti.service.api;

import io.netty.buffer.DefaultByteBufHolder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

public class FullApiInvocation<I> extends DefaultByteBufHolder implements ApiInvocation {
	
	private final ApiOperation<?,?,?> operation;
	private final HttpHeaders headers;
	private final FullHttpRequest request;
	private final ApiMacher matcher;
	
	public FullApiInvocation(ApiMacher matcher,FullHttpRequest request) {
		super(request.content().retain());
		this.headers=request.headers();
		this.request=request;
		this.operation=matcher.operation();
		this.matcher=matcher;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ApiOperation<I,?,?> operation() {
		return (ApiOperation<I, ?, ?>) operation;
	}

	@Override
	public HttpHeaders headers() {
		return headers;
	}

	@Override
	public FullHttpRequest request() {
		return request;
	}

	public ApiMacher matcher() {
		return matcher;
	}
	
}
