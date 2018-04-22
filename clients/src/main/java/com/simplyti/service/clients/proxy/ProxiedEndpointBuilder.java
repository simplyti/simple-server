package com.simplyti.service.clients.proxy;

import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.proxy.Proxy.ProxyType;

public class ProxiedEndpointBuilder {

	private final Endpoint target;

	public ProxiedEndpointBuilder(Endpoint target) {
		this.target=target;
	}

	public ProxiedEndpoint throughSocks5(String host, int port) {
		return through(host,port,ProxyType.SOCKS5);
	}

	public ProxiedEndpoint throughHTTP(String host, int port) {
		return through(host,port,ProxyType.HTTP);
	}
	
	public ProxiedEndpoint through(String host, int port, ProxyType type) {
		return new ProxiedEndpoint(target.schema(),target.address(),new Proxy(host, port,type));
	}

}
