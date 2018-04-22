package com.simplyti.service.clients.http;

import io.netty.channel.EventLoopGroup;

public class HttpClientBuilder {

	private EventLoopGroup eventLoopGroup;
	
	private boolean checkStatusCode;

	public HttpClientBuilder eventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup = eventLoopGroup;
		return this;
	}

	public HttpClient build() {
		return new DefaultHttpClient(eventLoopGroup,checkStatusCode);
	}

	public HttpClientBuilder withCheckStatusCode() {
		checkStatusCode = true;
		return this;
	}

}
