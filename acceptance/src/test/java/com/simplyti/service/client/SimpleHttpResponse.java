package com.simplyti.service.client;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class SimpleHttpResponse {
	
	private final String body;
	private final HttpResponseStatus status;
	private final HttpHeaders headers;

	public SimpleHttpResponse(String body,HttpResponseStatus status,HttpHeaders headers) {
		this.body=body;
		this.status=status;
		this.headers=headers;
	}

	public String body() {
		return body;
	}

	public int status() {
		return status.code();
	}

	public HttpHeaders headers() {
		return headers;
	}

}
