package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.Channel;
import lombok.experimental.Delegate;

public class UnpooledClientChannel implements ClientChannel {

	@Delegate
	private final Channel channel;
	private final Address address;

	public UnpooledClientChannel(Channel channel, Address address) {
		this.channel=channel;
		this.address=address;
	}

	@Override
	public Future<Void> release() {
		return new DefaultFuture<>(channel.close(),channel.eventLoop());
	}

	@Override
	public Address address() {
		return address;
	}

}
