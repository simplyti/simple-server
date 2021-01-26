package com.simplyti.service.clients.endpoint;

import com.simplyti.service.clients.Schema;
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
	
	private final Schema schema;
	private final Address address;
	
	public Endpoint(Schema schema,String host, int port) {
		this(schema,new Address(host,port));
	}
	
	public Endpoint(Schema schema,Address address) {
		this.schema=schema;
		this.address=address;
	}
	
	public boolean isProxied() {
		return this instanceof ProxiedEndpoint;
	}
	
	@Override
	public String toString() {
		return schema.name()+"://"+address;
	}

}
