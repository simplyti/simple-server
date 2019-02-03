package com.simplyti.service.clients.k8s.endpoints;

import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.Namespaced;
import com.simplyti.service.clients.k8s.endpoints.builder.EndpointBuilder;
import com.simplyti.service.clients.k8s.endpoints.domain.Endpoint;

public interface Endpoints extends Namespaced<Endpoint,NamespacedEndpoints>, K8sApi<Endpoint>{

	EndpointBuilder builder(String namespace);

}
