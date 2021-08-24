package com.simplyti.service.clients.http.request;

import com.simplyti.service.filter.http.HttpRequestFilter;

public interface FilterableRequestBuilder<T> {
	
	T withFilter(HttpRequestFilter filter);
	
}
