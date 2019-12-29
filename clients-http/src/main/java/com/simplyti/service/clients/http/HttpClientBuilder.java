package com.simplyti.service.clients.http;

import com.simplyti.service.clients.ClientBuilder;
import com.simplyti.service.clients.http.request.HttpRequestBuilder;

public interface HttpClientBuilder extends ClientBuilder<HttpClientBuilder,HttpClient,HttpRequestBuilder> {

	HttpClientBuilder withCheckStatusCode();

	HttpClientBuilder withBearerAuth(String token);

}
