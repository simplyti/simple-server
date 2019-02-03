package com.simplyti.service.clients.k8s.services.builder;

public interface ServicePortBuilder<T> {

	ServicePortBuilder<T> port(int port);
	ServicePortBuilder<T> name(String name);
	ServicePortBuilder<T> targetPort(int port);

	T create();

}
