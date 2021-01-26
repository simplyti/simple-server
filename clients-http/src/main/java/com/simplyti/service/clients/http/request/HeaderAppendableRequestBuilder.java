package com.simplyti.service.clients.http.request;

public interface HeaderAppendableRequestBuilder<T> {
	
	T withHeader(CharSequence name, CharSequence value);
	
	T withBasicAuth(String user, String pass);
	T withBearerAuth(String token);
	
}
