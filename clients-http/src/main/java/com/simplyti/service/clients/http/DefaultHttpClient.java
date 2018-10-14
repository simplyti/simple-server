package com.simplyti.service.clients.http;

import com.simplyti.service.clients.AbstractClient;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.channel.HttpChannelInitializer;

import io.netty.channel.EventLoopGroup;

public class DefaultHttpClient extends AbstractClient<HttpRequestBuilder> implements HttpClient {

	private final boolean checkStatusCode;
	private final Endpoint endpoint;

	public DefaultHttpClient(EventLoopGroup eventLoopGroup, Endpoint endpoint, boolean checkStatusCode) {
		super(eventLoopGroup,new HttpChannelInitializer());
		this.checkStatusCode=checkStatusCode;
		this.endpoint=endpoint;
	}

	@Override
	public HttpRequestBuilder request() {
		return new DefaultHttpRequestBuilder(client(),endpoint,checkStatusCode);
	}

}
