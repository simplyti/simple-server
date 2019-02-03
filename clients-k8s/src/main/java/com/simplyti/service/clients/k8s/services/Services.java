package com.simplyti.service.clients.k8s.services;

import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.Namespaced;
import com.simplyti.service.clients.k8s.services.builder.ServiceBuilder;
import com.simplyti.service.clients.k8s.services.domain.Service;

public interface Services extends Namespaced<Service,NamespacedServices>, K8sApi<Service>{

	ServiceBuilder builder(String namespace);
	
}
