package com.simplyti.service.api;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import io.netty.buffer.DefaultByteBufHolder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;

public class ApiInvocation<I> extends DefaultByteBufHolder {
	
	private final ApiOperation<I,?> operation;
	private final Matcher matcher;
	private final Map<String, List<String>> params;
	private final boolean keepAlive;
	private final String uri;
	private final HttpHeaders headers;
	private final FullHttpRequest request;
	
	public ApiInvocation(ApiOperation<I,?> operation,Matcher matcher,Map<String, List<String>> params, FullHttpRequest request) {
		super(request.content().retain());
		this.uri=request.uri();
		this.headers=request.headers();
		this.request=request;
		this.operation=operation;
		this.matcher=matcher;
		this.params=params;
		this.keepAlive=HttpUtil.isKeepAlive(request);
	}

	public ApiOperation<I,?> operation() {
		return operation;
	}

	public Matcher matcher() {
		return matcher;
	}

	public Map<String, List<String>> params() {
		return params;
	}
	
	public boolean isKeepAlive() {
		return keepAlive;
	}

	public String uri() {
		return uri;
	}

	public HttpHeaders headers() {
		return headers;
	}

	public FullHttpRequest request() {
		return request;
	}

}
