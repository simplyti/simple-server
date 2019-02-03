package com.simplyti.service.clients.http;

import com.simplyti.service.clients.ClientBuilder;
import com.simplyti.service.clients.Endpoint;

import io.netty.channel.EventLoopGroup;

public class HttpClientBuilder extends ClientBuilder<HttpClientBuilder>{

	private EventLoopGroup eventLoopGroup;
	private Endpoint endpoint;
	private boolean checkStatusCode;
	private String bearerAuth;

	public HttpClientBuilder eventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup = eventLoopGroup;
		return this;
	}

	public HttpClient build() {
		return new DefaultHttpClient(eventLoopGroup,endpoint,bearerAuth,checkStatusCode,poolConfig);
	}

	public HttpClientBuilder withEndpoint(Endpoint endpoint) {
		this.endpoint=endpoint;
		return this;
	}
	
	public HttpClientBuilder withCheckStatusCode() {
		checkStatusCode = true;
		return this;
	}

	public HttpClientBuilder withBearerAuth(String bearerAuth) {
		this.bearerAuth=bearerAuth;
		return this;
	}

}
