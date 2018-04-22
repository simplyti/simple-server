package com.simplyti.service.clients.http;

import com.simplyti.service.clients.AbstractClientRequestBuilder;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.request.DefaultFinishabBodyleHttpRequest;
import com.simplyti.service.clients.http.request.FinishableBodyHttpRequest;
import com.simplyti.service.clients.http.request.FinishableHttpRequest;
import com.simplyti.service.clients.http.request.DefaultFinishableHttpRequest;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public class DefaultHttpRequestBuilder extends AbstractClientRequestBuilder<HttpRequestBuilder> implements HttpRequestBuilder {

	private final InternalClient target;
	private final Endpoint endpoint;
	
	private boolean checkStatusCode;

	public DefaultHttpRequestBuilder(InternalClient target, Endpoint endpoint,boolean checkStatusCode) {
		this.target=target;
		this.endpoint=endpoint;
		this.checkStatusCode=checkStatusCode;
	}
	
	@Override
	public HttpRequestBuilder withCheckStatusCode() {
		this.checkStatusCode=true;
		return this;
	}

	@Override
	public FinishableHttpRequest get(String uri) {
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri, Unpooled.EMPTY_BUFFER);
		return new DefaultFinishableHttpRequest(target,endpoint,checkStatusCode,request,readTimeout());
	}

	@Override
	public FinishableBodyHttpRequest post(String uri) {
		return new DefaultFinishabBodyleHttpRequest(target,endpoint,checkStatusCode,HttpMethod.POST,uri,readTimeout());
	}

	@Override
	public FinishableHttpRequest sendFull(FullHttpRequest request) {
		return new DefaultFinishableHttpRequest(target,endpoint,checkStatusCode,request,readTimeout());
	}

}
