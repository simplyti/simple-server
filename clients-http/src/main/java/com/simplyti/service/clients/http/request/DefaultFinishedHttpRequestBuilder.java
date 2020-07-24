package com.simplyti.service.clients.http.request;

import java.util.List;
import java.util.Map;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

public class DefaultFinishedHttpRequestBuilder extends AbstractFinishableHttpRequestBuilder<FinishedHttpRequestBuilder> implements FinishedHttpRequestBuilder {

	private final FullHttpRequest request;

	public DefaultFinishedHttpRequestBuilder(ChannelProvider channelProvider, FullHttpRequest request, Map<String,Object> params, HttpHeaders headers, boolean checkStatus, List<HttpRequestFilter> filters) {
		super(channelProvider, null, null, params, headers, checkStatus, filters);
		this.request=request;
	}
	
	protected FullHttpRequest buildRequest(ClientChannel ch) {
		return request;
	}

	@Override
	protected ByteBuf body(ClientChannel ch) {
		return null;
	}

}
