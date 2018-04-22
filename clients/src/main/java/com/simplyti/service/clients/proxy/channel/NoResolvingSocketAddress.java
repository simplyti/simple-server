package com.simplyti.service.clients.proxy.channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.simplyti.service.clients.Address;

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
