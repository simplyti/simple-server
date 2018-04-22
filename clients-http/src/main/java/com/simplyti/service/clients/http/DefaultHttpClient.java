package com.simplyti.service.clients.http;

import com.simplyti.service.clients.AbstractClient;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.channel.HttpChannelInitializer;

import io.netty.channel.EventLoopGroup;

public class DefaultHttpClient extends AbstractClient<HttpRequestBuilder> implements HttpClient {

	private boolean checkStatusCode;

	public DefaultHttpClient(EventLoopGroup eventLoopGroup, boolean checkStatusCode) {
		super(eventLoopGroup,new HttpChannelInitializer());
		this.checkStatusCode=checkStatusCode;
	}

	@Override
	public HttpRequestBuilder withEndpoin(Endpoint endpoint) {
		return new DefaultHttpRequestBuilder(client(),endpoint,checkStatusCode);
	}

}
