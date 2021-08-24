package com.simplyti.service.clients.http.request;

import com.simplyti.service.clients.http.websocket.WebsocketClient;
import com.simplyti.service.clients.request.BaseClientRequestBuilder;

import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpRequestBuilder extends BaseClientRequestBuilder<HttpRequestBuilder>, HeaderAppendableRequestBuilder<HttpRequestBuilder>, ParamAppendableRequestBuilder<HttpRequestBuilder>, StatusCheckableRequestBuilder<HttpRequestBuilder>, FilterableRequestBuilder<HttpRequestBuilder> {

	FinishableHttpRequestBuilder get(String path);
	
	FinishableHttpRequestBuilder delete(String path);
	
	FinishablePayloadableHttpRequestBuilder post(String path);
	
	FinishablePayloadableHttpRequestBuilder put(String path);
	
	FinishablePayloadableHttpRequestBuilder patch(String path);
	
	FinishablePayloadableHttpRequestBuilder options(String path);
	
	FinishedHttpRequestBuilder send(FullHttpRequest fullRequest);

	WebsocketClient websocket(String uri);
	
}
