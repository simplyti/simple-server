package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;

public interface FinishablePayloadableHttpRequestBuilder extends BaseFinishableHttpRequestBuilder<FinishablePayloadableHttpRequestBuilder>, HeaderAppendableRequestBuilder<FinishablePayloadableHttpRequestBuilder> {

	FinishableHttpRequestBuilder withBody(Consumer<ByteBuf> bobyWriter);
	
	@Deprecated
	FinishableHttpRequestBuilder body(Consumer<ByteBuf> bobyWriter);

}
