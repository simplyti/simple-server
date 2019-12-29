package com.simplyti.service.clients.http.request;

import java.util.Map;

public interface HeaderAppendableRequestBuilder<T> {
	
	T withHeader(String name, String value);
	T withHeader(CharSequence name, String value);
	T withHeader(CharSequence name, CharSequence value);
	T withHeader(String name, CharSequence value);
	
	T withBasicAuth(String user, String pass);
	
	T params(Map<String, String> params);
	T param(String name, Object value);
	T param(String name);
	
}
