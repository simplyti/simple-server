package com.simplyti.service.clients.k8s.namespaces;

import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.domain.Status;
import com.simplyti.service.clients.k8s.namespaces.builder.NamespacesBuilder;
import com.simplyti.service.clients.k8s.namespaces.domain.Namespace;

import io.netty.util.concurrent.Future;

public interface Namespaces extends K8sApi<Namespace>{
	
	Future<Status> delete(String name);
	NamespacesBuilder builder();

}
