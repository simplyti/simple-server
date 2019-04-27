package com.simplyti.service.clients.k8s.services.builder;

import com.simplyti.service.clients.k8s.common.builder.K8sResourceBuilder;
import com.simplyti.service.clients.k8s.services.domain.Service;
import com.simplyti.service.clients.k8s.services.domain.ServiceType;

public interface ServiceBuilder extends K8sResourceBuilder<ServiceBuilder,Service>{

	public ServicePortBuilder<? extends ServiceBuilder> withPort();

	public ServiceBuilder withSelector(String name, String value);

	public ServiceBuilder withType(ServiceType type);

	public ServiceBuilder withClusterIp(String string);

	public ServiceBuilder withLoadBalancerIP(String ip);

}
