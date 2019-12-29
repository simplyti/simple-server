package com.simplyti.service.clients.request;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.util.concurrent.Future;


public interface GenericRequestBuilder extends BaseClientRequestBuilder<GenericRequestBuilder>{

	GenericRequestBuilder withChannelInitialize(Consumer<ClientChannel> consumer);
	
	Future<ClientChannel> channel();

}
