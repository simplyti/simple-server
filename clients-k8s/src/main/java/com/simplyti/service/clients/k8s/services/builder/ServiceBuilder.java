package com.simplyti.service.clients.k8s.services.builder;

import com.simplyti.service.clients.k8s.common.builder.K8sResourceBuilder;
import com.simplyti.service.clients.k8s.services.domain.Service;

public interface ServiceBuilder extends K8sResourceBuilder<ServiceBuilder,Service>{

	public ServicePortBuilder<? extends ServiceBuilder> withPort();

}
