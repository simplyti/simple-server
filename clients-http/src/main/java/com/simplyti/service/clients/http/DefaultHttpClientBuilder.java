package com.simplyti.service.clients.http;

import com.simplyti.service.clients.AbstractClientBuilder;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.http.request.HttpRequestBuilder;
import com.simplyti.service.clients.monitor.DefaultClientMonitor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.ssl.SslProvider;

public class DefaultHttpClientBuilder extends AbstractClientBuilder<HttpClientBuilder, HttpClient, HttpRequestBuilder>  implements HttpClientBuilder {

	private boolean checkStatus;
	private String token;

	@Override
	public HttpClientBuilder withCheckStatusCode() {
		this.checkStatus=true;
		return this;
	}
	
	@Override
	public HttpClientBuilder withBearerAuth(String token) {
		this.token=token;
		return this;
	}
	
	@Override
	protected HttpClient build0(EventLoopGroup eventLoopGroup, Bootstrap bootstrap, Endpoint endpoint, SslProvider sslProvider, DefaultClientMonitor monitor, int poolSize, boolean unpooledChannels,
			long poolIdleTimeout) {
		return new DefaultHttpClient(eventLoopGroup, bootstrap, endpoint, headers(), sslProvider, checkStatus, monitor, poolSize, unpooledChannels, poolIdleTimeout);
	}

	private HttpHeaders headers() {
		if(token!=null) {
			HttpHeaders headers = new DefaultHttpHeaders();
			headers.set(HttpHeaderNames.AUTHORIZATION,"Bearer "+token);
			return headers;
		}
		return null;
	}

}
