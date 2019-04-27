package com.simplyti.service.clients.k8s.endpoints.builder;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.k8s.endpoints.domain.Address;
import com.simplyti.service.clients.k8s.endpoints.domain.Port;
import com.simplyti.service.clients.k8s.endpoints.domain.Subset;

public class DefaultEndpointSubsetBuilder<T extends SubsetHolder<T>> implements EndpointSubsetBuilder<T> {

	private final T parent;
	
	private final List<Address> addresses = new ArrayList<>();
	private final List<Port> ports = new ArrayList<>();

	public DefaultEndpointSubsetBuilder(T parent) {
		this.parent=parent;
	}

	@Override
	public EndpointSubsetBuilder<T> withAddress(String ip) {
		addresses.add(new Address(ip));
		return this;
	}

	@Override
	public EndpointSubsetBuilder<T> withPort(int port) {
		ports.add(new Port(null, port, null));
		return this;
	}
	
	@Override
	public EndpointSubsetBuilder<T> withPort(int port, String name) {
		ports.add(new Port(name, port, null));
		return this;
	}
	
	@Override
	public EndpointSubsetBuilder<T> withPort(int port, String name, String protocol) {
		ports.add(new Port(name, port, protocol));
		return this;
	}

	@Override
	public T create() {
		return parent.addSubset(new Subset(addresses,ports));
	}

}
