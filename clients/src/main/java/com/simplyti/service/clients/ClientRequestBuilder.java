package com.simplyti.service.clients;

public interface ClientRequestBuilder<B extends ClientRequestBuilder<B>> {
	
	public B withReadTimeout(int i);

}
