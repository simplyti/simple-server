package com.simplyti.service.clients.http;

import java.util.Base64;

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
import io.netty.util.CharsetUtil;

public class DefaultHttpClientBuilder extends AbstractClientBuilder<HttpClientBuilder, HttpClient, HttpRequestBuilder>  implements HttpClientBuilder {

	private boolean checkStatus;
	private String bearerToken;
	
	private String basicUser;
	private String basicPass;
	
	@Override
	public HttpClientBuilder withCheckStatusCode() {
		this.checkStatus=true;
		return this;
	}
	
	@Override
	public HttpClientBuilder withBearerAuth(String token) {
		this.bearerToken=token;
		return this;
	}
	
	@Override
	public HttpClientBuilder withBasicAuth(String user, String password) {
		this.basicUser=user;
		this.basicPass=password;
		return this;
	}
	
	@Override
	protected HttpClient build0(EventLoopGroup eventLoopGroup, Bootstrap bootstrap, Endpoint endpoint, SslProvider sslProvider, DefaultClientMonitor monitor, int poolSize, boolean unpooledChannels, long poolIdleTimeout, long readTimeoutMilis) {
		return new DefaultHttpClient(eventLoopGroup, bootstrap, endpoint, headers(), sslProvider, checkStatus, monitor, poolSize, unpooledChannels, poolIdleTimeout, readTimeoutMilis);
	}
	
	private HttpHeaders headers() {
		if(bearerToken!=null) {
			HttpHeaders headers = new DefaultHttpHeaders();
			headers.set(HttpHeaderNames.AUTHORIZATION,"Bearer "+bearerToken);
			return headers;
		} else if(basicUser!=null && basicPass!=null) {
			HttpHeaders headers = new DefaultHttpHeaders();
			String userPass = basicUser+":"+basicPass;
			headers.set(HttpHeaderNames.AUTHORIZATION,"Basic "+Base64.getEncoder().encodeToString(userPass.getBytes(CharsetUtil.UTF_8)));
			return headers;
		}
		return null;
	}

}
