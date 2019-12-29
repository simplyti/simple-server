package com.simplyti.service.clients.http.request;

public interface StatusCheckableRequestBuilder<T> {
	
	T withCheckStatusCode();
	T withIgnoreStatusCode();

}
