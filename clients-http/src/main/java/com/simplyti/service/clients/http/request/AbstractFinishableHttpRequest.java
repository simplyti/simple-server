package com.simplyti.service.clients.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.simplyti.service.clients.ClientConfig;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.handler.DecodingFullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.HttpResponseHandler;
import com.simplyti.service.clients.http.handler.ServerEventResponseHandler;
import com.simplyti.service.clients.http.handler.StreamResponseHandler;
import com.simplyti.service.clients.http.sse.ServerEvent;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringEncoder;

public abstract class AbstractFinishableHttpRequest implements FinishableHttpRequest {
	
	protected final InternalClient client;
	private final boolean checkStatusCode;
	private final ClientConfig config;
	
	private final Map<String,Object> params;
	
	public AbstractFinishableHttpRequest(InternalClient client, boolean checkStatusCode, ClientConfig config) {
		this.client = client;
		this.config=config;
		this.checkStatusCode=checkStatusCode;
		this.params=new HashMap<>();
	}
	
	protected void setHostHeader(HttpRequest request) {
		if(!request.headers().contains(HttpHeaderNames.HOST)) {
			request.headers().set(HttpHeaderNames.HOST,config.endpoint().address().host());
		}
	}
	
	@Override
	public FinishableHttpRequest params(Map<String, String> params) {
		this.params.putAll(params);
		return this;
	}
	
	@Override
	public FinishableHttpRequest param(String name) {
		this.params.put(name, null);
		return this;
	}
	
	@Override
	public FinishableHttpRequest param(String name, Object value) {
		this.params.put(name, value);
		return this;
	}
	
	@Override
	public Future<FullHttpResponse> fullResponse() {
		return client.channel(channel->{
			channel.pipeline().addLast(new FullHttpResponseHandler<>(channel,checkStatusCode));
		},request(),config);
	}
	
	@Override
	public <T> Future<T> fullResponse(Function<FullHttpResponse, T> function) {
		return client.channel(channel->{
			channel.pipeline().addLast(new DecodingFullHttpResponseHandler<>(function,channel,checkStatusCode));
		},request(),config);
	}

	@Override
	public Future<Void> forEach(Consumer<HttpObject> consumer) {
		return client.channel(channel->{
			channel.pipeline().addLast(new HttpResponseHandler(channel,consumer));
		},request(),config);
	}

	@Override
	public Future<Void> stream(Consumer<ByteBuf> consumer) {
		return client.channel(channel->{
			channel.pipeline().addLast(new StreamResponseHandler(channel,consumer));
		},request(),config);
	}
	
	@Override
	public Future<Void> stream(String handlerName, ChannelHandler handler) {
		return client.channel(channel->{
			channel.pipeline().addLast(new StreamResponseHandler(channel,null));
			channel.pipeline().addLast(handlerName,handler);
		},request(),config);
	}
	
	@Override
	public Future<Void> sse(Consumer<ServerEvent> consumer) {
		return client.channel(channel->{
			channel.pipeline().addLast(new ServerEventResponseHandler(channel,consumer));
		},request(),config);
	}
	
	private FullHttpRequest request() {
		if(params.isEmpty()) {
			return request0();
		}
		
		FullHttpRequest request = request0();
		QueryStringEncoder encoder = new QueryStringEncoder(request.uri());
		params.forEach((name,value)->encoder.addParam(name, value!=null?value.toString():null));
		return request.setUri(encoder.toString());
	}
	
	protected abstract FullHttpRequest request0();

}
