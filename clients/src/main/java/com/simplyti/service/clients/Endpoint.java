package com.simplyti.service.clients;

import com.simplyti.service.clients.proxy.ProxiedEndpoint;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of="address")
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class Endpoint {
	
	private final Schema schema;
	private final Address address;
	
	public boolean isProxied() {
		return this instanceof ProxiedEndpoint;
	}

	public ProxiedEndpoint asProxied() {
		return ProxiedEndpoint.class.cast(this);
	}

}
