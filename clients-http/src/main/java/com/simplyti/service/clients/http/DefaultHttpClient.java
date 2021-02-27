package com.simplyti.service.clients.http;


import java.util.List;

import com.simplyti.service.clients.AbstractClient;
import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.http.request.DefaultHttpRequestBuilder;
import com.simplyti.service.clients.http.request.HttpRequestBuilder;
import com.simplyti.service.clients.monitor.DefaultClientMonitor;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.ssl.SslProvider;

public class DefaultHttpClient extends AbstractClient<HttpRequestBuilder> implements HttpClient {

	private final Endpoint endpoint;
	private final boolean checkStatusCode;
	private final HttpHeaders headers;
	
	public DefaultHttpClient(EventLoopGroup eventLoopGroup, Bootstrap bootstrap, Endpoint endpoint, HttpHeaders headers, SslProvider sslProvider, boolean checkStatusCode,
			DefaultClientMonitor monitor, int poolSize, boolean unpooledChannels, long poolIdleTimeout, long readTimeoutMillis, List<HttpRequestFilter> filters) {
		super(bootstrap,eventLoopGroup,unpooledChannels, new HttpClientChannelPoolHandler(readTimeoutMillis,filters), sslProvider, monitor , monitor, poolSize, poolIdleTimeout);
		this.endpoint=endpoint;
		this.headers=headers;
		this.checkStatusCode=checkStatusCode;
	}

	@Override
	public HttpRequestBuilder request0(ClientChannelFactory clientFactory) {
		return new DefaultHttpRequestBuilder(eventLoopGroup(), clientFactory,endpoint,headers,checkStatusCode);
	}

}
