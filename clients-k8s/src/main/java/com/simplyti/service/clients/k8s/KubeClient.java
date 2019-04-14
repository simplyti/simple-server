package com.simplyti.service.clients.k8s;

import com.simplyti.service.clients.k8s.endpoints.Endpoints;
import com.simplyti.service.clients.k8s.ingresses.Ingresses;
import com.simplyti.service.clients.k8s.namespaces.Namespaces;
import com.simplyti.service.clients.k8s.pods.Pods;
import com.simplyti.service.clients.k8s.secrets.Secrets;
import com.simplyti.service.clients.k8s.serviceaccounts.ServiceAccounts;
import com.simplyti.service.clients.k8s.services.Services;

import io.netty.util.concurrent.Future;

public interface KubeClient {
	
	public static KubeclientBuilder builder() {
		return new KubeclientBuilder();
	}
	
	public NamespacedClient namespace(String name);

	public Pods pods();

	public Services services();

	public Ingresses ingresses();

	public Endpoints endpoints();

	public Secrets secrets();
	
	public ServiceAccounts serviceAccounts();
	
	public Namespaces namespaces();

	public Future<String> health();

}
