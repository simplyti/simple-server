package com.simplyti.service.clients.endpoint;


import java.net.InetSocketAddress;
import java.net.SocketAddress;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class TcpAddress implements Address {
	
	private final String host;
	private final int port;
	
	@Override
	public String toString() {
		return host+":"+port;
	}

	@Override
	public SocketAddress toSocketAddress() {
		return new InetSocketAddress(host, port);
	}

}
