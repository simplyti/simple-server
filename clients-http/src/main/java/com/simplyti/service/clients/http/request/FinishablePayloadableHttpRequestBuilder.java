package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;
import java.util.function.Function;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.request.stream.StreamedBody;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface FinishablePayloadableHttpRequestBuilder extends BaseFinishableHttpRequestBuilder<FinishablePayloadableHttpRequestBuilder>, HeaderAppendableRequestBuilder<FinishablePayloadableHttpRequestBuilder>, FilterableRequestBuilder<FinishablePayloadableHttpRequestBuilder> {

	FinishableHttpRequestBuilder withBody(Consumer<ByteBuf> bobyWriter);
	
	StreamedBody withStreamBody(Consumer<ClientChannel> initializerxxxx);
	
	FinishableHttpRequestBuilder body(Function<ByteBufAllocator,ByteBuf> bobyWriter);

}
