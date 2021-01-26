package com.simplyti.service.clients.http;

import com.simplyti.service.clients.Client;
import com.simplyti.service.clients.http.request.HttpRequestBuilder;

public interface HttpClient extends Client<HttpRequestBuilder> {

	static HttpClientBuilder builder() {
		return new DefaultHttpClientBuilder();
	}
	
}
