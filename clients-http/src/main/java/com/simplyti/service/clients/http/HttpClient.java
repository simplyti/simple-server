package com.simplyti.service.clients.http;

import com.simplyti.service.clients.Client;

public interface HttpClient extends Client<HttpRequestBuilder> {
	
	public static HttpClientBuilder builder() {
		return new HttpClientBuilder();
	}

}
