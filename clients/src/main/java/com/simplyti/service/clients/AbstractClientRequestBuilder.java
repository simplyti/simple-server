package com.simplyti.service.clients;

public abstract class AbstractClientRequestBuilder<B extends ClientRequestBuilder<B>> implements ClientRequestBuilder<B>{
	
	private static final int DEFAULT_READ_TIMEOUT = 5000;
	private int readTimeout = DEFAULT_READ_TIMEOUT;

	@SuppressWarnings("unchecked")
	@Override
	public B withReadTimeout(int timeout) {
		this.readTimeout=timeout;
		return (B) this;
	}
	
	protected long readTimeout() {
		return readTimeout;
	}

}
