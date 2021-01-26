package com.simplyti.service.clients.http.request;

import java.util.Base64;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

public class HeaderAppendBuilder<T> implements HeaderAppendableRequestBuilder<T>{
	
	private final T parent;
	
	private HttpHeaders headers;

	public HeaderAppendBuilder(HttpHeaders headers,T parent) {
		this.parent=parent;
		this.headers=headers;
	}

	@Override
	public T withHeader(CharSequence name, CharSequence value) {
		initializeHeaders();
		headers.set(name, value);
		return parent;
	}

	@Override
	public T withBasicAuth(String user, String pass) {
		initializeHeaders();
		String userpass = user+":"+pass;
		this.headers.set(HttpHeaderNames.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes(CharsetUtil.UTF_8)));
		return parent;
	}
	
	@Override
	public T withBearerAuth(String token) {
		initializeHeaders();
		this.headers.set(HttpHeaderNames.AUTHORIZATION, "Bearer " + token);
		return parent;
	}
	
	private void initializeHeaders() {
		if(headers==null) {
			this.headers = new DefaultHttpHeaders();
		}
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void withHeaders(HttpRequest request) {
		if(headers!=null) {
			request.headers().set(headers);
		}
	}

}
