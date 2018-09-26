package com.simplyti.service.clients.http;

import java.util.Base64;
import java.util.function.Consumer;

import com.simplyti.service.clients.AbstractClientRequestBuilder;
import com.simplyti.service.clients.ClientRequestChannel;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.request.DefaultFinishabBodyleHttpRequest;
import com.simplyti.service.clients.http.request.FinishableBodyHttpRequest;
import com.simplyti.service.clients.http.request.FinishableHttpRequest;
import com.simplyti.service.clients.http.request.FinishableStreamedHttpRequest;
import com.simplyti.service.clients.http.ws.DefaultWebSocketClient;
import com.simplyti.service.clients.http.ws.WebSocketClient;
import com.simplyti.service.clients.http.ws.handler.WebSocketChannelHandler;
import com.simplyti.service.clients.http.request.DefaultFinishableHttpRequest;
import com.simplyti.service.clients.http.request.DefaultFinishableStreamedHttpRequest;

import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class DefaultHttpRequestBuilder extends AbstractClientRequestBuilder<HttpRequestBuilder> implements HttpRequestBuilder {

	private final InternalClient client;
	private final Endpoint endpoint;
	
	private final DefaultHttpHeaders headers;
	private boolean checkStatusCode;

	public DefaultHttpRequestBuilder(InternalClient target, Endpoint endpoint,boolean checkStatusCode) {
		this.client=target;
		this.endpoint=endpoint;
		this.checkStatusCode=checkStatusCode;
		this.headers = new DefaultHttpHeaders(true);
		headers.add(HttpHeaderNames.HOST,endpoint.address().host());
	}
	
	@Override
	public HttpRequestBuilder withHeader(String name, String value) {
		headers.add(name,value);
		return this;
	}
	
	@Override
	public HttpRequestBuilder withCheckStatusCode() {
		this.checkStatusCode=true;
		return this;
	}
	
	@Override
	public HttpRequestBuilder withIgnoreStatusCode() {
		this.checkStatusCode=false;
		return this;
	}

	@Override
	public FinishableHttpRequest get(String uri) {
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri, Unpooled.EMPTY_BUFFER,
				headers,EmptyHttpHeaders.INSTANCE);
		return new DefaultFinishableHttpRequest(client,endpoint,checkStatusCode,request,config());
	}
	
	@Override
	public FinishableHttpRequest delete(String uri) {
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.DELETE, uri, Unpooled.EMPTY_BUFFER,
				headers,EmptyHttpHeaders.INSTANCE);
		return new DefaultFinishableHttpRequest(client,endpoint,checkStatusCode,request,config());
	}

	@Override
	public FinishableBodyHttpRequest post(String uri) {
		return new DefaultFinishabBodyleHttpRequest(client,endpoint,checkStatusCode,HttpMethod.POST,uri,headers,config());
	}
	
	@Override
	public FinishableBodyHttpRequest put(String uri) {
		return new DefaultFinishabBodyleHttpRequest(client,endpoint,checkStatusCode,HttpMethod.PUT,uri,headers,config());
	}

	@Override
	public FinishableHttpRequest sendFull(FullHttpRequest request) {
		return new DefaultFinishableHttpRequest(client,endpoint,checkStatusCode,request,config());
	}

	@Override
	public HttpRequestBuilder withBasicAuth(String user, String password) {
		String userpass = user+":"+password;
		headers.set(HttpHeaderNames.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes(CharsetUtil.UTF_8)));
		return this;
	}

	@Override
	public FinishableStreamedHttpRequest send(HttpRequest request) {
		return new DefaultFinishableStreamedHttpRequest(client,endpoint,request,config());
	}

	@Override
	public WebSocketClient websocket(String uri, Consumer<WebSocketFrame> consumer) {
		EventLoop executor = client.eventLoopGroup().next();
		Promise<Void> promise = executor.newPromise();
		Future<ClientRequestChannel<Void>> clientChannel = client.channel(config(),endpoint,channel->{
			channel.pipeline().addLast(new HttpObjectAggregator(65536));
			channel.pipeline().addLast(new WebSocketChannelHandler(endpoint,uri,headers,channel,consumer));
		},promise);
		return new DefaultWebSocketClient(clientChannel,executor);
	}

}
