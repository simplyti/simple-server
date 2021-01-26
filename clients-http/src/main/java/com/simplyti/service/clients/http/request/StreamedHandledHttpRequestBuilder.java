package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.ClientChannelInitializer;

public interface StreamedHandledHttpRequestBuilder<T> extends StreamedFinalHandledHttpRequestBuilder<T> {

	<U> StreamedFinalHandledHttpRequestBuilder<U> withInitializer(ClientChannelInitializer initializer);

	StreamedHandledHttpRequestBuilder<T> onConnect(Consumer<ClientChannel> consumer);

}
