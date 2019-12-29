package com.simplyti.service.clients.http.request;

import com.simplyti.service.clients.http.stream.request.StreamedInputHttpRequestBuilder;
import com.simplyti.service.clients.http.websocket.WebsocketClient;
import com.simplyti.service.clients.request.BaseClientRequestBuilder;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

public interface HttpRequestBuilder extends BaseClientRequestBuilder<HttpRequestBuilder>, HeaderAppendableRequestBuilder<HttpRequestBuilder>, StatusCheckableRequestBuilder<HttpRequestBuilder> {

	FinishableHttpRequestBuilder get(String path);
	FinishableHttpRequestBuilder get();
	
	FinishableHttpRequestBuilder delete(String path);
	
	FinishablePayloadableHttpRequestBuilder post(String path);
	
	FinishablePayloadableHttpRequestBuilder put(String path);
	
	FinishablePayloadableHttpRequestBuilder patch(String path);
	
	FinishablePayloadableHttpRequestBuilder options(String path);
	
	FinishedHttpRequestBuilder send(FullHttpRequest fullRequest);
	StreamedInputHttpRequestBuilder send(HttpRequest request);
	
	WebsocketClient websocket();

}
