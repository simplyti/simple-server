package com.simplyti.service.clients.k8s.services.builder;

import com.simplyti.service.clients.k8s.services.domain.ServicePort;

public interface ServicePortHolder<T extends ServicePortHolder<T>> {

	T addPort(ServicePort port);

}
