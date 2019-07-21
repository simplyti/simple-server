package com.simplyti.service.clients;

import java.util.concurrent.TimeUnit;

public class ClientPoolConfigBuilder<B extends ClientBuilder<B>> {

	private final ClientBuilder<B> builder;
	private Long maxIdle;
	private Integer size;
	
	public ClientPoolConfigBuilder(ClientBuilder<B> builder) {
		this.builder=builder;
	}

	public ClientPoolConfigBuilder<B> maxIdle(Integer maxIdle, TimeUnit unit) {
		this.maxIdle=unit.toSeconds(maxIdle);
		return this;
	}

	public B end() {
		return builder.poolConfig(new PoolConfig(maxIdle==null?-1:maxIdle,size));
	}

	public ClientPoolConfigBuilder<B> size(int size) {
		this.size =size;
		return this;
	}

}
