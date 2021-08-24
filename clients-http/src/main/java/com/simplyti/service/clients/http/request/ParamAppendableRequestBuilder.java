package com.simplyti.service.clients.http.request;

import java.util.Map;

public interface ParamAppendableRequestBuilder<T> {
	
	T params(Map<String, ?> params);
	T param(String name, Object value);
	T param(String name);

}
