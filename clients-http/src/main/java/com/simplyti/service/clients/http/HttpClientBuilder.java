package com.simplyti.service.clients.http;

import com.simplyti.service.clients.ClientBuilder;
import com.simplyti.service.clients.http.request.HttpRequestBuilder;
import com.simplyti.service.filter.http.HttpRequestFilter;

public interface HttpClientBuilder extends ClientBuilder<HttpClientBuilder,HttpClient,HttpRequestBuilder> {

	HttpClientBuilder withCheckStatusCode();

	HttpClientBuilder withBearerAuth(String token);

	HttpClientBuilder withBasicAuth(String user, String password);

	ClientBuilder<HttpClientBuilder, HttpClient, HttpRequestBuilder> withFilter(HttpRequestFilter filter);

}
