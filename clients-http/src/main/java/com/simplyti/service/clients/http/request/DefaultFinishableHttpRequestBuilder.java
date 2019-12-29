package com.simplyti.service.clients.http.request;

import java.util.Map;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.request.ChannelProvider;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

public class DefaultFinishableHttpRequestBuilder extends AbstractFinishableHttpRequestBuilder<FinishableHttpRequestBuilder> implements FinishableHttpRequestBuilder {

	public DefaultFinishableHttpRequestBuilder(ChannelProvider channelProvider, HttpMethod method, String path, Map<String,Object> params, HttpHeaders headers, boolean checkStatus) {
		super(channelProvider, method, path, params, headers, checkStatus);
	}

	@Override
	protected ByteBuf body(ClientChannel ch) {
		return null;
	}

}
