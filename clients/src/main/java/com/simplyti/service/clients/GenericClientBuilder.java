package com.simplyti.service.clients;

import java.util.function.Consumer;

import com.simplyti.service.clients.request.GenericRequestBuilder;

import io.netty.channel.Channel;

public interface GenericClientBuilder extends ClientBuilder< GenericClientBuilder, GenericClient, GenericRequestBuilder> {

	GenericClientBuilder withInitializer(Consumer<Channel> init);

}
