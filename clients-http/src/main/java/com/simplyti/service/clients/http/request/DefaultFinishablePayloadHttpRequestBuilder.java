package com.simplyti.service.clients.http.request;

import java.util.Map;
import java.util.function.Function;

import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultFinishablePayloadHttpRequestBuilder implements FinishableHttpRequestBuilder {

	private FinishablePayloadableHttpRequestBuilder target;

	public DefaultFinishablePayloadHttpRequestBuilder(FinishablePayloadableHttpRequestBuilder target) {
		this.target=target;
	}

	@Override
	public Future<FullHttpResponse> fullResponse() {
		return target.fullResponse();
	}

	@Override
	public <T> Future<T> fullResponse(Function<FullHttpResponse, T> fn) {
		return target.fullResponse(fn);
	}
	
	@Override
	public ServerSentEventRequestBuilder serverSentEvents() {
		return target.serverSentEvents();
	}
	
	@Override
	public StreamedHandledHttpRequestBuilder<ByteBuf> stream() {
		return target.stream();
	}

	@Override
	public FinishableHttpRequestBuilder withHeader(CharSequence name, CharSequence value) {
		target.withHeader(name, value);
		return this;
	}
	
	@Override
	public FinishableHttpRequestBuilder withBasicAuth(String user, String pass) {
		target.withBasicAuth(user, pass);
		return this;
	}
	
	@Override
	public FinishableHttpRequestBuilder withBearerAuth(String token) {
		target.withBearerAuth(token);
		return this;
	}

	@Override
	public FinishableHttpRequestBuilder withCheckStatusCode() {
		target.withCheckStatusCode();
		return this;
	}

	@Override
	public FinishableHttpRequestBuilder params(Map<String, ?> params) {
		target.params(params);
		return this;
	}
	
	@Override
	public FinishableHttpRequestBuilder param(String name, Object value) {
		target.param(name,value);
		return this;
	}

	@Override
	public FinishableHttpRequestBuilder param(String name) {
		target.param(name);
		return this;
	}

	@Override
	public FinishableHttpRequestBuilder withIgnoreStatusCode() {
		target.withIgnoreStatusCode();
		return this;
	}

	@Override
	public FinishableHttpRequestBuilder withFilter(HttpRequestFilter filter) {
		target.withFilter(filter);
		return this;
	}

}