package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public interface ClientChannel extends Channel {
	
	public static final AttributeKey<Address> ADDRESS = AttributeKey.valueOf("clients.address");
	
	Address address();

	Future<Void> release();

}
