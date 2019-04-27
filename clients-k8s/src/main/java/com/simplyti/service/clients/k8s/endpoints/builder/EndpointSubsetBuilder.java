package com.simplyti.service.clients.k8s.endpoints.builder;

public interface EndpointSubsetBuilder<T> {

	EndpointSubsetBuilder<T> withAddress(String address);

	EndpointSubsetBuilder<T> withPort(int port);
	
	EndpointSubsetBuilder<T> withPort(int port, String name);
	
	EndpointSubsetBuilder<T> withPort(int port, String name, String protocol);

	T create();

}
