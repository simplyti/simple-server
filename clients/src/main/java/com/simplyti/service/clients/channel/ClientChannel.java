package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.Channel;

public interface ClientChannel extends Channel {
	
	Address address();

	Future<Void> release();

}
