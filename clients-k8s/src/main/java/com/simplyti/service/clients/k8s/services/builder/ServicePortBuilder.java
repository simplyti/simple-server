package com.simplyti.service.clients.k8s.services.builder;

import com.simplyti.service.clients.k8s.services.domain.ServiceProtocol;

public interface ServicePortBuilder<T> {

	ServicePortBuilder<T> port(int port);
	ServicePortBuilder<T> name(String name);
	ServicePortBuilder<T> targetPort(Object port);
	ServicePortBuilder<T> protocol(ServiceProtocol protocol);

	T create();

}
