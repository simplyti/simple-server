package com.simplyti.service.clients.k8s;

import com.simplyti.service.clients.k8s.endpoints.NamespacedEndpoints;
import com.simplyti.service.clients.k8s.jobs.NamespacedJobs;
import com.simplyti.service.clients.k8s.pods.NamespacedPods;
import com.simplyti.service.clients.k8s.services.NamespacedServices;

public interface NamespacedClient {
	
	public NamespacedServices services();

	public NamespacedEndpoints endpoints();
	
	public NamespacedPods pods();

	public NamespacedJobs jobs();

}
