package com.simplyti.service.clients.http.request;

import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.http.websocket.DefaultWebsocketClient;
import com.simplyti.service.clients.http.websocket.WebsocketClient;
import com.simplyti.service.clients.request.AbstractClientRequestBuilder;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import lombok.experimental.Delegate;

public class DefaultHttpRequestBuilder extends AbstractClientRequestBuilder<HttpRequestBuilder> implements HttpRequestBuilder {

	@Delegate(excludes = ParamsAppendBuilder.class)
	private final HeaderAppendBuilder<HttpRequestBuilder> headerAppend;
	
	@Delegate(excludes = HeaderAppendBuilder.class)
	private final ParamsAppendBuilder<HttpRequestBuilder> paramsAppend;
	
	private final EventLoopGroup eventLoopGroup;
	
	private boolean checkStatus;

	
	public DefaultHttpRequestBuilder(EventLoopGroup eventLoopGroup, ClientChannelFactory clientChannelFactory, Endpoint endpoint, HttpHeaders headers, boolean checkStatus) {
		super(clientChannelFactory, endpoint);
		this.headerAppend=new HeaderAppendBuilder<>(headers,this);
		this.paramsAppend=new ParamsAppendBuilder<>(null, this);
		this.checkStatus=checkStatus;
		this.eventLoopGroup=eventLoopGroup;
	}

	@Override
	public FinishableHttpRequestBuilder get(String path) {
		return new DefaultFinishableHttpRequestBuilder(this, eventLoopGroup, HttpMethod.GET,path,paramsAppend.getParams(),headerAppend.getHeaders(),checkStatus);
	}
	
	@Override
	public FinishableHttpRequestBuilder delete(String path) {
		return new DefaultFinishableHttpRequestBuilder(this,eventLoopGroup,HttpMethod.DELETE,path,paramsAppend.getParams(),headerAppend.getHeaders(),checkStatus);
	}

	@Override
	public FinishablePayloadableHttpRequestBuilder post(String path) {
		return new DefaultFinishablePayloadableHttpRequestBuilder(this,eventLoopGroup,HttpMethod.POST,path,paramsAppend.getParams(),headerAppend.getHeaders(),checkStatus);
	}
	
	@Override
	public FinishablePayloadableHttpRequestBuilder put(String path) {
		return new DefaultFinishablePayloadableHttpRequestBuilder(this,eventLoopGroup,HttpMethod.PUT,path,paramsAppend.getParams(),headerAppend.getHeaders(),checkStatus);
	}
	
	@Override
	public FinishablePayloadableHttpRequestBuilder patch(String path) {
		return new DefaultFinishablePayloadableHttpRequestBuilder(this,eventLoopGroup,HttpMethod.PATCH,path,paramsAppend.getParams(),headerAppend.getHeaders(),checkStatus);
	}
	
	@Override
	public FinishablePayloadableHttpRequestBuilder options(String path) {
		return new DefaultFinishablePayloadableHttpRequestBuilder(this,eventLoopGroup,HttpMethod.OPTIONS,path,paramsAppend.getParams(),headerAppend.getHeaders(),checkStatus);
	}
	
	@Override
	public FinishedHttpRequestBuilder send(FullHttpRequest fullRequest) {
		return new DefaultFinishedHttpRequestBuilder(this,eventLoopGroup,fullRequest,paramsAppend.getParams(),headerAppend.getHeaders(),checkStatus);
	}
	
	@Override
	public HttpRequestBuilder withCheckStatusCode() {
		this.checkStatus=true;
		return this;
	}
	
	@Override
	public HttpRequestBuilder withIgnoreStatusCode() {
		this.checkStatus=false;
		return this;
	}

	@Override
	public WebsocketClient websocket(String uri) {
		return new DefaultWebsocketClient(uri, eventLoopGroup.next(), this);
	}
	
}
