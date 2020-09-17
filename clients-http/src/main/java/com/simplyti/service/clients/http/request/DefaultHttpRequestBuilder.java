package com.simplyti.service.clients.http.request;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.http.stream.request.DefaultStreamedOutputHttpRequestBuilder;
import com.simplyti.service.clients.http.stream.request.StreamedInputHttpRequestBuilder;
import com.simplyti.service.clients.http.websocket.DefaultWebsocketClient;
import com.simplyti.service.clients.http.websocket.WebsocketClient;
import com.simplyti.service.clients.request.AbstractClientRequestBuilder;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

public class DefaultHttpRequestBuilder extends AbstractClientRequestBuilder<HttpRequestBuilder> implements HttpRequestBuilder {

	private static final String ROOT = "/";
	
	private final EventLoopGroup eventLoopGroup;
	
	private List<HttpRequestFilter> filters;
	
	private boolean checkStatus;
	private HttpHeaders headers;
	private Map<String,Object> params;


	
	public DefaultHttpRequestBuilder(EventLoopGroup eventLoopGroup, ClientChannelFactory clientChannelFactory, Endpoint endpoint, HttpHeaders headers, boolean checkStatus,
			List<HttpRequestFilter> filters) {
		super(clientChannelFactory, endpoint);
		this.eventLoopGroup=eventLoopGroup;
		this.checkStatus=checkStatus;
		this.headers=headers;
		this.filters=filters;
	}

	@Override
	public FinishableHttpRequestBuilder get(String path) {
		return new DefaultFinishableHttpRequestBuilder(this, HttpMethod.GET,path,params,headers,checkStatus, filters);
	}
	
	@Override
	public FinishableHttpRequestBuilder get() {
		return new DefaultFinishableHttpRequestBuilder(this,HttpMethod.GET,ROOT,params,headers,checkStatus, filters);
	}
	
	@Override
	public FinishableHttpRequestBuilder delete(String path) {
		return new DefaultFinishableHttpRequestBuilder(this,HttpMethod.DELETE,path,params,headers,checkStatus, filters);
	}

	@Override
	public FinishablePayloadableHttpRequestBuilder post(String path) {
		return new DefaultFinishablePayloadableHttpRequestBuilder(this,HttpMethod.POST,path,params,headers,checkStatus, filters);
	}
	
	@Override
	public FinishablePayloadableHttpRequestBuilder put(String path) {
		return new DefaultFinishablePayloadableHttpRequestBuilder(this,HttpMethod.PUT,path,params,headers,checkStatus, filters);
	}
	
	@Override
	public FinishablePayloadableHttpRequestBuilder patch(String path) {
		return new DefaultFinishablePayloadableHttpRequestBuilder(this,HttpMethod.PATCH,path,params,headers,checkStatus, filters);
	}
	
	@Override
	public FinishablePayloadableHttpRequestBuilder options(String path) {
		return new DefaultFinishablePayloadableHttpRequestBuilder(this,HttpMethod.OPTIONS,path,params,headers,checkStatus, filters);
	}
	
	@Override
	public FinishedHttpRequestBuilder send(FullHttpRequest fullRequest) {
		return new DefaultFinishedHttpRequestBuilder(this,fullRequest,params,headers,checkStatus, filters);
	}
	
	@Override
	public StreamedInputHttpRequestBuilder send(HttpRequest request) {
		return new DefaultStreamedOutputHttpRequestBuilder(this,request,params,headers,checkStatus,eventLoopGroup.next());
	}
	
	@Override
	public WebsocketClient websocket() {
		return websocket(ROOT);
	}
	
	@Override
	public WebsocketClient websocket(String uri) {
		return new DefaultWebsocketClient(uri,this,eventLoopGroup.next());
	}
	
	@Override
	public HttpRequestBuilder withHeader(String name, String value) {
		initializeHeaders();
		headers.set(name, value);
		return this;
	}

	@Override
	public HttpRequestBuilder withHeader(CharSequence name, String value) {
		initializeHeaders();
		headers.set(name, value);
		return this;
	}

	@Override
	public HttpRequestBuilder withHeader(CharSequence name, CharSequence value) {
		initializeHeaders();
		headers.set(name, value);
		return this;
	}

	@Override
	public HttpRequestBuilder withHeader(String name, CharSequence value) {
		initializeHeaders();
		headers.set(name, value);
		return this;
	}
	
	@Override
	public HttpRequestBuilder withBasicAuth(String user, String pass) {
		initializeHeaders();
		String userpass = user+":"+pass;
		this.headers.set(HttpHeaderNames.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes(CharsetUtil.UTF_8)));
		return this;
	}
	
	@Override
	public HttpRequestBuilder withBearerAuth(String token) {
		initializeHeaders();
		this.headers.set(HttpHeaderNames.AUTHORIZATION, "Bearer " + token);
		return this;
	}
	
	@Override
	public HttpRequestBuilder params(Map<String, String> params) {
		initializeParams();
		this.params.putAll(params);
		return this;
	}
	
	@Override
	public HttpRequestBuilder param(String name, Object value) {
		initializeParams();
		this.params.put(name, value);
		return this;
	}

	@Override
	public HttpRequestBuilder param(String name) {
		initializeParams();
		this.params.put(name, null);
		return this;
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
	public HttpRequestBuilder withFilter(HttpRequestFilter filter) {
		if(filters == null) {
			this.filters = new ArrayList<>();
		}
		this.filters.add(filter);
		return this;
	}
	
	private void initializeHeaders() {
		if(headers==null) {
			this.headers = new DefaultHttpHeaders();
		}
	}
	
	private void initializeParams() {
		if(params==null) {
			this.params=new HashMap<>();
		}
	}

}
