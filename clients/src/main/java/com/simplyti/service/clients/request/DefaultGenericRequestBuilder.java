package com.simplyti.service.clients.request;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.util.concurrent.Future;

import io.netty.util.AttributeKey;

public class DefaultGenericRequestBuilder extends AbstractClientRequestBuilder<GenericRequestBuilder> implements GenericRequestBuilder {

	private static final AttributeKey<Boolean> INITALIZED = AttributeKey.newInstance("client.init");
	
	private Consumer<ClientChannel> initalizer;

	public DefaultGenericRequestBuilder(ClientChannelFactory clientChannelFactory, Endpoint endpoint) {
		super(clientChannelFactory, endpoint);
	}

	@Override
	public GenericRequestBuilder withChannelInitialize(Consumer<ClientChannel> initalizer) {
		this.initalizer=initalizer;
		return this;
	}
	
	@Override
	public Future<ClientChannel> channel() {
		return super.channel().thenApply(this::initialize);
	}

	private ClientChannel initialize(ClientChannel channel) {
		Boolean initalized = channel.attr(INITALIZED).get();
		if(initalized==null) {
			initalizer.accept(channel);
			channel.attr(INITALIZED).set(true);
		}
		return channel;
	}


}
