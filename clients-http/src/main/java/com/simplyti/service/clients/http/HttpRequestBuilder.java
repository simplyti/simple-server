package com.simplyti.service.clients.http;

import com.simplyti.service.clients.ClientRequestBuilder;
import com.simplyti.service.clients.http.request.FinishableBodyHttpRequest;
import com.simplyti.service.clients.http.request.FinishableHttpRequest;

import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpRequestBuilder extends ClientRequestBuilder<HttpRequestBuilder>  {
	
	public HttpRequestBuilder withCheckStatusCode();
	
	public FinishableHttpRequest get(String path);

	public FinishableBodyHttpRequest post(String path);

	public FinishableHttpRequest sendFull(FullHttpRequest retain);

}
