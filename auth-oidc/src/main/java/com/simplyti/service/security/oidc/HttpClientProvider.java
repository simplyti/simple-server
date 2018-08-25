package com.simplyti.service.security.oidc;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.clients.http.HttpClient;

import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class HttpClientProvider implements Provider<HttpClient>{
	
	private final EventLoopGroup eventLoopGroup;

	@Override
	public HttpClient get() {
		return HttpClient.builder().eventLoopGroup(eventLoopGroup).build();
	}

}
