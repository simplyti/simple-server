package com.simplyti.service.clients.endpoint;

import java.net.SocketAddress;

import io.netty.channel.unix.DomainSocketAddress;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class UnixAddress implements Address {
	
	private final String file;
	
	@Override
	public String toString() {
		return file;
	}

	@Override
	public SocketAddress toSocketAddress() {
		return new DomainSocketAddress(file);
	}

}
