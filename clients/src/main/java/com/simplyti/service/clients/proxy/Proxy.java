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
	private final String username;
	private final String password;
	
	public Proxy(String host,int port,ProxyType type) {
		this(host,port,type,null,null);
	}
	
	public Proxy(String host,int port,ProxyType type, String username, String password) {
		this.address=new Address(host, port);
		this.type=type;
		this.username=username;
		this.password=password;
	}

}
