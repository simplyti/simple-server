package com.simplyti.service.clients.proxy;

import com.simplyti.service.clients.Address;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Proxy {
	
	public enum ProxyType{
		SOCKS5,
		HTTP
	}

	private final Address address;
	private final ProxyType type;
	
	public Proxy(String host,int port,ProxyType type) {
		this.address=new Address(host, port);
		this.type=type;
	}

}
