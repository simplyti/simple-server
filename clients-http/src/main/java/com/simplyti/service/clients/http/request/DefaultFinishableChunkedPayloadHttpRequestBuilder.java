package com.simplyti.service.clients.http.request;

import java.util.List;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public class DefaultFinishableChunkedPayloadHttpRequestBuilder extends AbstractFinishableHttpRequestBuilder<FinishableHttpRequestBuilder> implements FinishableHttpRequestBuilder {

	private final Consumer<ChunckedBodyRequest> chunkedConsumer;

	public DefaultFinishableChunkedPayloadHttpRequestBuilder(Consumer<ChunckedBodyRequest> chunkedConsumer, ChannelProvider channelProvider, String path, HttpMethod method, ParamsAppendBuilder<FinishableHttpRequestBuilder> paramsAppend, HeaderAppendBuilder<FinishableHttpRequestBuilder> headerAppend, boolean checkStatus,
			List<HttpRequestFilter> filters) {
		super(channelProvider,method,path, paramsAppend, headerAppend, checkStatus, filters);
		this.chunkedConsumer=chunkedConsumer;
	}

	protected void connectSuccess(ClientChannel channel) {
		chunkedConsumer.accept(new DefaultChunckedBodyRequest(channel));
	}

	@Override
	public HttpRequest request(boolean expectedContinue, ByteBuf buff) {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, path);
		headerAppend.withHeaders(request);
		request.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
		return request;
	}
	
	@Override
	public ServerSentEventRequestBuilder serverSentEvents() {
		return new DefaultServerSentEventRequestBuilder(channelProvider, chunkedConsumer, this, checkStatus, filters);
	}
	
	@Override
	public StreamedHandledHttpRequestBuilder<ByteBuf> stream() {
		return new DefaultStreamedHandledHttpRequestBuilder(channelProvider, chunkedConsumer, this, checkStatus, filters);
	}

	@Override
	protected ByteBuf body(ClientChannel ch) {
		return null;
	}

}