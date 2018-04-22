package com.simplyti.service.builder.di;


import javax.inject.Inject;
import javax.inject.Provider;
import javax.net.ssl.KeyManagerFactory;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.vavr.control.Try;

public class SslContextProvider implements Provider<SslContext>{
	
	@Inject
	private KeyManagerFactory keyManagerFactory;

	@Override
	public SslContext get() {
		return Try.of(()->SslContextBuilder.forServer(keyManagerFactory).build()).get();
	}
	
}
