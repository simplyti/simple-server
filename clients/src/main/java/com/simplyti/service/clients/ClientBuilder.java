package com.simplyti.service.clients;


public class ClientBuilder<B extends ClientBuilder<B>> implements PoolConfigAware {
	
	protected PoolConfig poolConfig;
	
	public ClientPoolConfigBuilder<B> withPool() {
		return new ClientPoolConfigBuilder<>(this,this);
	}

	public void poolConfig(PoolConfig poolConfig) {
		this.poolConfig=poolConfig;
	}

}
