package com.simplyti.service.clients.http.request;

import java.util.Map;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.request.ChannelProvider;

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

public class DefaultFinishedHttpRequestBuilder extends AbstractFinishableHttpRequestBuilder<FinishedHttpRequestBuilder> implements FinishedHttpRequestBuilder {

	private final FullHttpRequest request;

	public DefaultFinishedHttpRequestBuilder(ChannelProvider channelProvider, EventLoopGroup eventLoopGroup, FullHttpRequest request, Map<String,Object> params, HttpHeaders headers, boolean checkStatus) {
		super(channelProvider, null, null, params, headers, checkStatus);
		this.request=request;
	}
	
	@Override
	public HttpRequest request(boolean expectedContinue, ByteBuf buff) {
		return setHeaders(request, request.content());
	}

	@Override
	protected ByteBuf body(ClientChannel ch) {
		return null;
	}

}
