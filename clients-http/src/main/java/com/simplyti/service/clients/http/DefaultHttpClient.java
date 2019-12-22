package com.simplyti.service.clients.http;

import com.simplyti.service.clients.AbstractClient;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.PoolConfig;
import com.simplyti.service.clients.http.channel.HttpChannelInitializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;

public class DefaultHttpClient extends AbstractClient<HttpRequestBuilder> implements HttpClient {

	private final boolean checkStatusCode;
	private final Endpoint endpoint;
	private final String authHeader;
	
	public DefaultHttpClient(SslProvider sslProvider, EventLoopGroup eventLoopGroup, Endpoint endpoint, String authHeader, boolean checkStatusCode,
			ChannelFactory<Channel> channelFactory, PoolConfig poolConfig) {
		super(sslProvider, eventLoopGroup,new HttpChannelInitializer(),channelFactory, poolConfig);
		this.checkStatusCode=checkStatusCode;
		this.endpoint=endpoint;
		this.authHeader=authHeader;
	}

	@Override
	public HttpRequestBuilder request() {
		return new DefaultHttpRequestBuilder(client(),endpoint,authHeader,checkStatusCode);
	}

}
