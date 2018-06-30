package com.simplyti.service.clients.http;

import java.util.Base64;

import com.google.common.base.Joiner;
import com.simplyti.service.clients.AbstractClientRequestBuilder;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.request.DefaultFinishabBodyleHttpRequest;
import com.simplyti.service.clients.http.request.FinishableBodyHttpRequest;
import com.simplyti.service.clients.http.request.FinishableHttpRequest;
import com.simplyti.service.clients.http.request.FinishableStreamedHttpRequest;
import com.simplyti.service.clients.http.request.DefaultFinishableHttpRequest;
import com.simplyti.service.clients.http.request.DefaultFinishableStreamedHttpRequest;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class DefaultHttpRequestBuilder extends AbstractClientRequestBuilder<HttpRequestBuilder> implements HttpRequestBuilder {

	private final InternalClient target;
	private final Endpoint endpoint;
	
	private final DefaultHttpHeaders headers;
	private boolean checkStatusCode;

	public DefaultHttpRequestBuilder(InternalClient target, Endpoint endpoint,boolean checkStatusCode) {
		this.target=target;
		this.endpoint=endpoint;
		this.checkStatusCode=checkStatusCode;
		this.headers = new DefaultHttpHeaders(true);
		headers.add(HttpHeaderNames.HOST,endpoint.address().host());
	}
	
	@Override
	public HttpRequestBuilder withCheckStatusCode() {
		this.checkStatusCode=true;
		return this;
	}

	@Override
	public FinishableHttpRequest get(String uri) {
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri, Unpooled.EMPTY_BUFFER,
				headers,EmptyHttpHeaders.INSTANCE);
		return new DefaultFinishableHttpRequest(target,endpoint,checkStatusCode,request,readTimeout());
	}

	@Override
	public FinishableBodyHttpRequest post(String uri) {
		return new DefaultFinishabBodyleHttpRequest(target,endpoint,checkStatusCode,HttpMethod.POST,uri,headers,readTimeout());
	}

	@Override
	public FinishableHttpRequest sendFull(FullHttpRequest request) {
		return new DefaultFinishableHttpRequest(target,endpoint,checkStatusCode,request,readTimeout());
	}

	@Override
	public HttpRequestBuilder withBasicAuth(String user, String password) {
		String userpass = Joiner.on(':').join(user,password);
		headers.set(HttpHeaderNames.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes(CharsetUtil.UTF_8)));
		return this;
	}

	@Override
	public FinishableStreamedHttpRequest send(HttpRequest request) {
		return new DefaultFinishableStreamedHttpRequest(target,endpoint,request,readTimeout());
	}

}
