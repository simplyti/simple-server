package com.simplyti.service.clients.k8s.common;

public interface Namespaced<T extends K8sResource, N extends NamespacedK8sApi<T>> {

	public N namespace(String namespace);
	
}
