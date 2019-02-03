package com.simplyti.service.clients.k8s.endpoints.builder;

import com.simplyti.service.clients.k8s.common.builder.K8sResourceBuilder;
import com.simplyti.service.clients.k8s.endpoints.domain.Endpoint;

public interface EndpointBuilder  extends K8sResourceBuilder<EndpointBuilder,Endpoint> {

	EndpointSubsetBuilder<? extends EndpointBuilder> withSubset();

}
