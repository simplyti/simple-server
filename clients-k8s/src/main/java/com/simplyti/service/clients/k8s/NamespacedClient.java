package com.simplyti.service.clients.k8s;

import com.simplyti.service.clients.k8s.endpoints.NamespacedEndpoints;
import com.simplyti.service.clients.k8s.services.NamespacedServices;

public interface NamespacedClient {
	
	public NamespacedServices services();

	public NamespacedEndpoints endpoints();

}
