package com.simplyti.service.clients.k8s;

import com.simplyti.service.clients.k8s.endpoints.NamespacedEndpoints;
import com.simplyti.service.clients.k8s.jobs.NamespacedJobs;
import com.simplyti.service.clients.k8s.pods.NamespacedPods;
import com.simplyti.service.clients.k8s.services.NamespacedServices;

public class DefaultNamespacedClient implements NamespacedClient {

	private final String namespace;
	private  KubeClient client;

	public DefaultNamespacedClient(String namespace, KubeClient client) {
		this.namespace=namespace;
		this.client=client;
	}

	@Override
	public NamespacedServices services() {
		return client.services().namespace(namespace);
	}

	@Override
	public NamespacedEndpoints endpoints() {
		return client.endpoints().namespace(namespace);
	}

	@Override
	public NamespacedPods pods() {
		return client.pods().namespace(namespace);
	}

	@Override
	public NamespacedJobs jobs() {
		return client.jobs().namespace(namespace);
	}

}
