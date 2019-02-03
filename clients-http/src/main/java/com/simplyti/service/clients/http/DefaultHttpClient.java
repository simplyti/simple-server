package com.simplyti.service.clients.http;

import com.simplyti.service.clients.AbstractClient;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.PoolConfig;
import com.simplyti.service.clients.http.channel.HttpChannelInitializer;

import io.netty.channel.EventLoopGroup;

public class DefaultHttpClient extends AbstractClient<HttpRequestBuilder> implements HttpClient {

	private final boolean checkStatusCode;
	private final Endpoint endpoint;
	private final String bearerAuth;

	public DefaultHttpClient(EventLoopGroup eventLoopGroup, Endpoint endpoint, String bearerAuth, boolean checkStatusCode,
			PoolConfig poolConfig) {
		super(eventLoopGroup,new HttpChannelInitializer(),poolConfig);
		this.checkStatusCode=checkStatusCode;
		this.endpoint=endpoint;
		this.bearerAuth=bearerAuth;
	}

	@Override
	public HttpRequestBuilder request() {
		return new DefaultHttpRequestBuilder(client(),endpoint,bearerAuth,checkStatusCode);
	}

}
