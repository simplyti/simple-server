package com.simplyti.service.clients.k8s.services.updater;

import java.util.Collections;

import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.updater.AbstractK8sResourceUpdater;
import com.simplyti.service.clients.k8s.common.updater.JsonPatch;
import com.simplyti.service.clients.k8s.services.builder.DefaultServicePortBuilder;
import com.simplyti.service.clients.k8s.services.builder.ServicePortBuilder;
import com.simplyti.service.clients.k8s.services.builder.ServicePortHolder;
import com.simplyti.service.clients.k8s.services.domain.Service;
import com.simplyti.service.clients.k8s.services.domain.ServicePort;

public class DefaultServiceUpdater extends AbstractK8sResourceUpdater<Service> implements ServicesUpdater, ServicePortHolder<DefaultServiceUpdater> {

	public DefaultServiceUpdater(HttpClient client, K8sAPI api, String namespace, String resource, String name) {
		super(client, api, namespace, resource, name, Service.class);
	}

	@Override
	public ServicePortBuilder<? extends ServicesUpdater> setPort() {
		return new DefaultServicePortBuilder<>(this);
	}

	@Override
	public DefaultServiceUpdater addPort(ServicePort port) {
		addPatch(JsonPatch.replace("/spec/ports", Collections.singleton(port)));
		return this;
	}

}
