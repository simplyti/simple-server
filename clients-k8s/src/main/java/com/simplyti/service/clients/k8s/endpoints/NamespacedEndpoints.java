package com.simplyti.service.clients.k8s.endpoints;

import com.simplyti.service.clients.k8s.common.NamespacedK8sApi;
import com.simplyti.service.clients.k8s.endpoints.builder.EndpointBuilder;
import com.simplyti.service.clients.k8s.endpoints.domain.Endpoint;
import com.simplyti.service.clients.k8s.endpoints.updater.EndpointUpdater;

public interface NamespacedEndpoints extends NamespacedK8sApi<Endpoint> {

	EndpointBuilder builder();

	EndpointUpdater update(String name);

}
