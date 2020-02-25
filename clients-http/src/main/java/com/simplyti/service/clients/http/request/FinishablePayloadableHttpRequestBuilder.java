package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface FinishablePayloadableHttpRequestBuilder extends BaseFinishableHttpRequestBuilder<FinishablePayloadableHttpRequestBuilder>, HeaderAppendableRequestBuilder<FinishablePayloadableHttpRequestBuilder> {

	FinishableHttpRequestBuilder withBody(Consumer<ByteBuf> bobyWriter);
	
	@Deprecated
	FinishableHttpRequestBuilder body(Function<ByteBufAllocator,ByteBuf> bobyWriter);

}
