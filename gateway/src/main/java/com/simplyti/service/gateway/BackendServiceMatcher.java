package com.simplyti.service.gateway;

import io.netty.handler.codec.http.HttpRequest;

public interface BackendServiceMatcher {
	
	public boolean matches();

	public BackendService get();

	public HttpRequest rewrite(HttpRequest request);

}
