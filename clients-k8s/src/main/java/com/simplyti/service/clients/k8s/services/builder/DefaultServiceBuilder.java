package com.simplyti.service.clients.k8s.services.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.common.builder.AbstractK8sResourceBuilder;
import com.simplyti.service.clients.k8s.services.domain.Service;
import com.simplyti.service.clients.k8s.services.domain.ServicePort;
import com.simplyti.service.clients.k8s.services.domain.ServiceSpec;
import com.simplyti.service.clients.k8s.services.domain.ServiceType;

public class DefaultServiceBuilder extends AbstractK8sResourceBuilder<ServiceBuilder,Service> implements ServiceBuilder, ServicePortHolder<DefaultServiceBuilder> {

	public static final String KIND = "Service";
	private final List<ServicePort> ports = new ArrayList<>();
	private final Map<String,String> selector = new HashMap<>();
	
	private ServiceType serviceType;
	private String clusterIp;
	private String loadBalancerIP;

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
				.selector(selector)
				.type(serviceType)
				.clusterIP(clusterIp)
				.loadBalancerIP(loadBalancerIP)
				.ports(ports).build(),null);
	}

	public DefaultServiceBuilder addPort(ServicePort port) {
		ports.add(port);
		return this;
	}

	@Override
	public ServiceBuilder withSelector(String name, String value) {
		selector.put(name, value);
		return this;
	}

	@Override
	public ServiceBuilder withType(ServiceType type) {
		this.serviceType=type;
		return this;
	}

	@Override
	public ServiceBuilder withClusterIp(String ip) {
		this.clusterIp=ip;
		return this;
	}

	@Override
	public ServiceBuilder withLoadBalancerIP(String ip) {
		this.loadBalancerIP=ip;
		return this;
	}

}
