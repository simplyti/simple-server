package com.simplyti.service.clients.k8s.services.updater;

import java.util.Collections;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.updater.AbstractK8sResourceUpdater;
import com.simplyti.service.clients.k8s.common.updater.JsonPatch;
import com.simplyti.service.clients.k8s.services.builder.DefaultServicePortBuilder;
import com.simplyti.service.clients.k8s.services.builder.ServicePortBuilder;
import com.simplyti.service.clients.k8s.services.builder.ServicePortHolder;
import com.simplyti.service.clients.k8s.services.domain.Service;
import com.simplyti.service.clients.k8s.services.domain.ServicePort;
import com.simplyti.service.clients.k8s.services.domain.ServiceType;

public class DefaultServiceUpdater extends AbstractK8sResourceUpdater<Service> implements ServicesUpdater, ServicePortHolder<DefaultServiceUpdater> {

	private boolean adding;

	public DefaultServiceUpdater(HttpClient client, Json json, K8sAPI api, String namespace, String resource, String name) {
		super(client,json, api, namespace, resource, name, Service.class);
	}

	@Override
	public ServicePortBuilder<? extends ServicesUpdater> setPort() {
		return new DefaultServicePortBuilder<>(this);
	}
	
	@Override
	public ServicePortBuilder<? extends ServicesUpdater> addPort() {
		this.adding=true;
		return new DefaultServicePortBuilder<>(this);
	}

	@Override
	public DefaultServiceUpdater addPort(ServicePort port) {
		if(adding) {
			adding=false;
			addPatch(JsonPatch.add("/spec/ports/-", port));
		}else {
			addPatch(JsonPatch.replace("/spec/ports", Collections.singleton(port)));
		}
		return this;
	}

	@Override
	public ServicesUpdater setType(ServiceType clusterip) {
		addPatch(JsonPatch.replace("/spec/type", clusterip));
		return this;
	}

}
