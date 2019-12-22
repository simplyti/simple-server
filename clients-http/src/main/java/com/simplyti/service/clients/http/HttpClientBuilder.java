package com.simplyti.service.clients.http;

import java.util.Base64;

import com.simplyti.service.clients.ClientBuilder;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.channel.ClientChannelFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.CharsetUtil;

public class HttpClientBuilder extends ClientBuilder<HttpClientBuilder>{

	private EventLoopGroup eventLoopGroup;
	private Endpoint endpoint;
	private boolean checkStatusCode;
	private String authHeader;
	private SslProvider sslProvider;
	private ChannelFactory<Channel> channelFactory;

	public HttpClientBuilder eventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup = eventLoopGroup;
		return this;
	}

	public HttpClient build() {
		return new DefaultHttpClient(sslProvider,eventLoopGroup,endpoint,authHeader,checkStatusCode,channelFactory(channelFactory),poolConfig);
	}
	
	public HttpClientBuilder withSslProvider(SslProvider sslProvider) {
		this.sslProvider=sslProvider;
		return this;
	}

	public HttpClientBuilder withEndpoint(Endpoint endpoint) {
		this.endpoint=endpoint;
		return this;
	}
	
	public HttpClientBuilder withChannelFactory(ChannelFactory<Channel> channelFactory) {
		this.channelFactory=channelFactory;
		return this;
	}
	
	public HttpClientBuilder withCheckStatusCode() {
		checkStatusCode = true;
		return this;
	}

	public HttpClientBuilder withBearerAuth(String bearerAuth) {
		this.authHeader="Bearer " + bearerAuth;
		return this;
	}
	
	public HttpClientBuilder withBasicAuth(String user,String password) {
		String userpass = user+":"+password;
		this.authHeader= "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes(CharsetUtil.UTF_8));
		return this;
	}
	
	private ChannelFactory<Channel> channelFactory(ChannelFactory<Channel> channelFactory) {
		if(channelFactory!=null) {
			return channelFactory;
		}
		return new ClientChannelFactory(eventLoopGroup);
	}

}
