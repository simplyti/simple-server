package com.simplyti.service.clients;


public class ClientBuilder<B extends ClientBuilder<B>> {
	
	protected PoolConfig poolConfig;
	
	public ClientPoolConfigBuilder<B> withPool() {
		return new ClientPoolConfigBuilder<>(this);
	}

	@SuppressWarnings("unchecked")
	public B poolConfig(PoolConfig poolConfig) {
		this.poolConfig=poolConfig;
		return (B) this;
	}

}
