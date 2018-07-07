package com.simplyti.service.clients.init;

import com.simplyti.service.clients.ClientRequestChannel;

public interface ClientRequestChannelInitializer<T> {

	void initialize(ClientRequestChannel<T> channel);

}
