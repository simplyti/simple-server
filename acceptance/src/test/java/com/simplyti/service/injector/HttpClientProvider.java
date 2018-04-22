package com.simplyti.service.injector;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.clients.http.HttpClient;

import io.netty.channel.EventLoopGroup;

public class HttpClientProvider implements Provider<HttpClient>{

	@Inject
	private EventLoopGroup eventLoopGroup;

	@Override
	public HttpClient get() {
		return HttpClient.builder()
				.eventLoopGroup(eventLoopGroup)
				.withCheckStatusCode()
				.build();
	}

}
