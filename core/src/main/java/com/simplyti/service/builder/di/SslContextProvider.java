package com.simplyti.service.builder.di;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import com.simplyti.service.ssl.SslConfig;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SslContextProvider implements Provider<SslContext>{
	
	private final KeyManagerFactory keyManagerFactory;
	private final TrustManagerFactory trustManagerFactory;
	private final SslConfig config;

	@Override
	@SneakyThrows
	public SslContext get() {
		return SslContextBuilder.forServer(keyManagerFactory)
				.sslProvider(config.sslProvider())
				.trustManager(trustManagerFactory)
				.clientAuth(config.clientAuth())
				.build();
	}
	
}
