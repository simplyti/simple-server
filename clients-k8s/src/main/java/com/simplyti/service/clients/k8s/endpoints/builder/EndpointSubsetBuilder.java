package com.simplyti.service.clients.k8s.endpoints.builder;

public interface EndpointSubsetBuilder<T> {

	EndpointSubsetBuilder<T> withAddress(String address);

	EndpointSubsetBuilder<T> withPort(int port);

	T create();

}
