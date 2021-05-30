package com.simplyti.service.clients.proxy;

import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.proxy.Proxy.ProxyType;

public class ProxiedEndpointBuilder {

	private final Endpoint target;
	private String username;
	private String password;

	public ProxiedEndpointBuilder(Endpoint target) {
		this.target=target;
	}
	
	public ProxiedEndpointBuilder withUsername(String username) {
		this.username=username;
		return this;
	}
	
	public ProxiedEndpointBuilder withPassword(String password) {
		this.password=password;
		return this;
	}

	public ProxiedEndpoint throughSocks5(String host, int port) {
		return through(host,port,ProxyType.SOCKS5);
	}
	
	public ProxiedEndpoint throughSocks4(String host, int port) {
		return through(host,port,ProxyType.SOCKS4);
	}

	public ProxiedEndpoint throughHTTP(String host, int port) {
		return through(host,port,ProxyType.HTTP);
	}
	
	public ProxiedEndpoint through(String host, int port, ProxyType type) {
		return new ProxiedEndpoint(target.scheme(),target.address(),new Proxy(host, port, type, username, password));
	}

}
