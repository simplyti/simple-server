package com.simplyti.service.clients.http.request;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.request.ChannelProvider;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

public class DefaultFinishablePayloadableHttpRequestBuilder extends AbstractFinishableHttpRequestBuilder<FinishablePayloadableHttpRequestBuilder> implements FinishablePayloadableHttpRequestBuilder {

	private final Map<String, Object> params;
	private final HttpHeaders headers;

	private Consumer<ByteBuf> bobyWriter;
	private Function<ByteBufAllocator,ByteBuf> bodySupplier;

	public DefaultFinishablePayloadableHttpRequestBuilder(ChannelProvider channelProvider, EventLoopGroup eventLoopGroup, HttpMethod method, String path, Map<String,Object> params, HttpHeaders headers, boolean checkStatus) {
		super(channelProvider,method,path, params, headers, checkStatus);
		this.params=params;
		this.headers=headers;
	}
	
	@Override
	public FinishableHttpRequestBuilder withBodySupplier(Function<ByteBufAllocator,ByteBuf> bodySupplier) {
		this.bodySupplier=bodySupplier;
		return new DefaultFinishablePayloadHttpRequestBuilder(this);
	}
	
	@Override
	public FinishableHttpRequestBuilder withChunkedBody(Consumer<ChunckedBodyRequest> chunkedConsumer) {
		return new DefaultFinishableChunkedPayloadHttpRequestBuilder(chunkedConsumer,channelProvider, path, method, params,headers,checkStatus);
	}

	@Override
	public FinishableHttpRequestBuilder withBodyWriter(Consumer<ByteBuf> bobyWriter) {
		this.bobyWriter=bobyWriter;
		return new DefaultFinishablePayloadHttpRequestBuilder(this);
		
	}
	
	@Override
	protected ByteBuf body(ClientChannel ch) {
		if(bobyWriter!=null) {
			ByteBuf buff = ch.alloc().buffer();
			bobyWriter.accept(buff);
			return buff;
		} else if(bodySupplier !=null) {
			return bodySupplier.apply(ch.alloc());
		}else {
			return null;
		}
	}
	
}
