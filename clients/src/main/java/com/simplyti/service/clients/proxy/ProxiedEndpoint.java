package com.simplyti.service.clients.proxy;

import com.simplyti.service.clients.Scheme;
import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.service.clients.endpoint.Endpoint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true,of= {})
public class ProxiedEndpoint extends Endpoint {

	private final Proxy proxy;

	public ProxiedEndpoint(Scheme schema, Address address, Proxy proxy) {
		super(schema, address);
		this.proxy=proxy;
	}

	public static ProxiedEndpointBuilder of(Endpoint target) {
		return new ProxiedEndpointBuilder(target);
	}
	
}
