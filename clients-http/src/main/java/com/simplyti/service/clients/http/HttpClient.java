package com.simplyti.service.clients.http;

import com.simplyti.service.clients.Client;
import com.simplyti.service.clients.http.request.HttpRequestBuilder;

import io.netty.channel.EventLoopGroup;

public interface HttpClient extends Client<HttpRequestBuilder> {

	static HttpClientBuilder builder() {
		return new DefaultHttpClientBuilder();
	}
	
	EventLoopGroup eventLoopGroup();

}
