package com.simplyti.service.clients;

import java.util.concurrent.TimeUnit;

public class ClientPoolConfigBuilder<B> {

	private final B parent;
	private final PoolConfigAware builder;
	private Long maxIdle;
	private Integer size;
	
	public ClientPoolConfigBuilder(B parent, PoolConfigAware builder) {
		this.parent=parent;
		this.builder=builder;
	}

	public ClientPoolConfigBuilder<B> maxIdle(Integer maxIdle, TimeUnit unit) {
		this.maxIdle=unit.toSeconds(maxIdle);
		return this;
	}

	public B end() {
		builder.poolConfig(new PoolConfig(maxIdle==null?-1:maxIdle,size));
		return parent;
	}

	public ClientPoolConfigBuilder<B> size(int size) {
		this.size =size;
		return this;
	}

}
