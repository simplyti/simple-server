package com.simplyti.service.clients.proxy;

import com.simplyti.service.clients.endpoint.TcpAddress;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Proxy {
	
	public enum ProxyType{
		SOCKS5,
		SOCKS4,
		HTTP
	}

	private final TcpAddress address;
	private final ProxyType type;
	private final String username;
	private final String password;
	
	public Proxy(String host,int port,ProxyType type) {
		this(host,port,type,null,null);
	}
	
	public Proxy(String host,int port,ProxyType type, String username, String password) {
		this.address=new TcpAddress(host, port);
		this.type=type;
		this.username=username;
		this.password=password;
	}

}
