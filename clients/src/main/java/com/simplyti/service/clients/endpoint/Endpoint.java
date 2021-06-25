package com.simplyti.service.clients.endpoint;

import com.simplyti.service.clients.Scheme;
import com.simplyti.service.clients.proxy.ProxiedEndpoint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(of="address")
@Getter
@Accessors(fluent = true)
@SuperBuilder
public class Endpoint {
	
	private final Scheme scheme;
	private final Address address;
	
	public Endpoint(Scheme schema, String host, int port) {
		this(schema,new TcpAddress(host,port));
	}
	
	public Endpoint(Scheme scheme, Address address) {
		this.scheme=scheme;
		this.address=address;
	}
	
	public boolean isProxied() {
		return this instanceof ProxiedEndpoint;
	}
	
	@Override
	public String toString() {
		return scheme.name()+"://"+address;
	}

}
