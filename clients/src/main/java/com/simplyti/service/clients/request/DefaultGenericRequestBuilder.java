package com.simplyti.service.clients.request;

import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.endpoint.Endpoint;

public class DefaultGenericRequestBuilder extends AbstractClientRequestBuilder<GenericRequestBuilder> implements GenericRequestBuilder {

	public DefaultGenericRequestBuilder(ClientChannelFactory clientChannelFactory, Endpoint endpoint) {
		super(clientChannelFactory, endpoint);
	}

}
