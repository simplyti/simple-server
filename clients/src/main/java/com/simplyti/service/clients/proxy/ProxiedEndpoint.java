package com.simplyti.service.clients.proxy;

import com.simplyti.service.clients.Address;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.Schema;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true,of= {})
public class ProxiedEndpoint extends Endpoint {

	private final Proxy proxy;

	public ProxiedEndpoint(Schema schema, Address address, Proxy proxy) {
		super(schema, address);
		this.proxy=proxy;
	}

	public static ProxiedEndpointBuilder of(Endpoint target) {
		return new ProxiedEndpointBuilder(target);
	}
	
}
