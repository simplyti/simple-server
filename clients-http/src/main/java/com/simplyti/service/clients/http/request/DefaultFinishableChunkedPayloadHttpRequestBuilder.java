package com.simplyti.service.clients.http.request;

import java.util.Map;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.request.ChannelProvider;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public class DefaultFinishableChunkedPayloadHttpRequestBuilder extends AbstractFinishableHttpRequestBuilder<FinishableHttpRequestBuilder> implements FinishableHttpRequestBuilder {

	private final Consumer<ChunckedBodyRequest> chunkedConsumer;

	public DefaultFinishableChunkedPayloadHttpRequestBuilder(Consumer<ChunckedBodyRequest> chunkedConsumer, ChannelProvider channelProvider, String path, HttpMethod method, Map<String,Object> params, HttpHeaders headers, boolean checkStatus) {
		super(channelProvider,method,path, params, headers, checkStatus);
		this.chunkedConsumer=chunkedConsumer;
	}

	protected void connectSuccess(ClientChannel channel) {
		chunkedConsumer.accept(new DefaultChunckedBodyRequest(channel));
	}

	@Override
	public HttpRequest request(ClientChannel channel) {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path);
		request.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
		return request;
	}
	
	@Override
	public StreamedHandledHttpRequestBuilder<ByteBuf> stream() {
		return new DefaultStreamedHandledHttpRequestBuilder(channelProvider, chunkedConsumer, this, checkStatus);
	}

	@Override
	protected ByteBuf body(ClientChannel ch) {
		return null;
	}

}