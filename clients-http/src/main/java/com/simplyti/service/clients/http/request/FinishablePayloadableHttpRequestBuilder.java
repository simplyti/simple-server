package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface FinishablePayloadableHttpRequestBuilder extends BaseFinishableHttpRequestBuilder<FinishablePayloadableHttpRequestBuilder>, HeaderAppendableRequestBuilder<FinishablePayloadableHttpRequestBuilder>, ParamAppendableRequestBuilder<FinishablePayloadableHttpRequestBuilder>, FilterableRequestBuilder<FinishablePayloadableHttpRequestBuilder> {

	FinishableHttpRequestBuilder withBodyWriter(Consumer<ByteBuf> bobyWriter);
	
	FinishableHttpRequestBuilder withBodySupplier(Function<ByteBufAllocator,ByteBuf> bobyWriter);

	FinishableHttpRequestBuilder withChunkedBody(Consumer<ChunckedBodyRequest> chunkedConsumer);

}
