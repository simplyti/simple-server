package com.simplyti.service.clients.channel.proxy;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.simplyti.service.clients.endpoint.Address;

@SuppressWarnings("serial")
public class NoResolvingSocketAddress extends SocketAddress {

	private InetSocketAddress wrapped;

	public NoResolvingSocketAddress(Address address) {
		this.wrapped=InetSocketAddress.createUnresolved(address.host(), address.port());
	}

	public SocketAddress unwrap() {
		return wrapped;
	}

}
