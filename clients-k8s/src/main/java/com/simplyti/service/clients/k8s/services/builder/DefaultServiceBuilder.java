package com.simplyti.service.clients.k8s.services.builder;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.common.builder.AbstractK8sResourceBuilder;
import com.simplyti.service.clients.k8s.services.domain.Service;
import com.simplyti.service.clients.k8s.services.domain.ServicePort;
import com.simplyti.service.clients.k8s.services.domain.ServiceSpec;

public class DefaultServiceBuilder extends AbstractK8sResourceBuilder<ServiceBuilder,Service> implements ServiceBuilder, ServicePortHolder<DefaultServiceBuilder> {

	public static final String KIND = "Service";
	private final List<ServicePort> ports = new ArrayList<>();

	public DefaultServiceBuilder(HttpClient client,K8sAPI api,String namespace, String resource) {
		super(client,api,namespace,resource,Service.class);
	}

	@Override
	public ServicePortBuilder<? extends ServiceBuilder> withPort() {
		return new DefaultServicePortBuilder<>(this);
	}

	@Override
	protected Service resource(K8sAPI api, Metadata metadata) {
		return new Service(KIND, api.version(), metadata, ServiceSpec.builder()
				.ports(ports).build(),null);
	}

	public DefaultServiceBuilder addPort(ServicePort port) {
		ports.add(port);
		return this;
	}

}
