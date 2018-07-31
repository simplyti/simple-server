package com.simplyti.service.clients.http;

import java.util.function.Consumer;

import com.simplyti.service.clients.ClientRequestBuilder;
import com.simplyti.service.clients.http.request.FinishableBodyHttpRequest;
import com.simplyti.service.clients.http.request.FinishableHttpRequest;
import com.simplyti.service.clients.http.request.FinishableStreamedHttpRequest;
import com.simplyti.service.clients.http.ws.WebSocketClient;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface HttpRequestBuilder extends ClientRequestBuilder<HttpRequestBuilder>  {
	
	public HttpRequestBuilder withCheckStatusCode();
	
	public HttpRequestBuilder withIgnoreStatusCode();
	
	public HttpRequestBuilder withBasicAuth(String user, String password);
	
	public FinishableHttpRequest get(String path);
	
	public FinishableHttpRequest delete(String path);

	public FinishableBodyHttpRequest post(String path);
	
	public FinishableBodyHttpRequest put(String path);

	public FinishableHttpRequest sendFull(FullHttpRequest request);

	public FinishableStreamedHttpRequest send(HttpRequest request);

	public WebSocketClient websocket(String uri, Consumer<WebSocketFrame> consumer);

}
