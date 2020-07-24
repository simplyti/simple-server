package com.simplyti.service.clients.http.request;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.sse.ServerSentEvents;
import com.simplyti.service.clients.http.stream.HttpInputStream;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

public class DefaultFinishablePayloadableHttpRequestBuilder extends AbstractFinishableHttpRequestBuilder<FinishablePayloadableHttpRequestBuilder> implements FinishablePayloadableHttpRequestBuilder {

	private Consumer<ByteBuf> bodyBuilder;
	private Function<ByteBufAllocator,ByteBuf> bodyBuilderSupplier;

	public DefaultFinishablePayloadableHttpRequestBuilder(ChannelProvider channelProvider, HttpMethod method, String path, Map<String,Object> params, HttpHeaders headers, boolean checkStatus, List<HttpRequestFilter> filters) {
		super(channelProvider,method,path, params, headers, checkStatus,filters);
	}

	@Override
	public FinishableHttpRequestBuilder withBody(Consumer<ByteBuf> bobyWriter) {
		this.bodyBuilder=bobyWriter;
		return new DefaultFinishablePayloadHttpRequestBuilder(this);
	}
	
	@Override
	public FinishableHttpRequestBuilder body(Function<ByteBufAllocator,ByteBuf> fn) {
		this.bodyBuilderSupplier=fn;
		return new DefaultFinishablePayloadHttpRequestBuilder(this);
	}

	@Override
	protected ByteBuf body(ClientChannel ch) {
		if(bodyBuilder!=null) {
			ByteBuf buff = ch.alloc().buffer();
			bodyBuilder.accept(buff);
			return buff;
		} else if(bodyBuilderSupplier !=null) {
			return bodyBuilderSupplier .apply(ch.alloc());
		}else {
			return null;
		}
	}
	
	private static final class DefaultFinishablePayloadHttpRequestBuilder implements FinishableHttpRequestBuilder {

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
		public FinishableHttpRequestBuilder withHeader(String name, String value) {
			target.withHeader(name, value);
			return this;
		}

		@Override
		public FinishableHttpRequestBuilder withHeader(CharSequence name, String value) {
			target.withHeader(name, value);
			return this;
		}

		@Override
		public FinishableHttpRequestBuilder withHeader(CharSequence name, CharSequence value) {
			target.withHeader(name, value);
			return this;
		}

		@Override
		public FinishableHttpRequestBuilder withHeader(String name, CharSequence value) {
			target.withHeader(name, value);
			return this;
		}
		
		@Override
		public FinishableHttpRequestBuilder withCheckStatusCode() {
			target.withCheckStatusCode();
			return this;
		}

		@Override
		public HttpInputStream stream() {
			return target.stream();
		}

		@Override
		public FinishableHttpRequestBuilder params(Map<String, String> params) {
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

		@Override
		public ServerSentEvents sse() {
			return target.sse();
		}
		
		@Override
		public FinishableHttpRequestBuilder withBasicAuth(String user, String pass) {
			target.withBasicAuth(user, pass);
			return this;
		}

	}

}
