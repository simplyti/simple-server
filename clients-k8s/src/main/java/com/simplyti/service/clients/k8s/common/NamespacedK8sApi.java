package com.simplyti.service.clients.k8s.common;

import com.simplyti.service.clients.k8s.common.domain.Status;
import com.simplyti.service.clients.k8s.common.list.KubeList;

import io.netty.util.concurrent.Future;

public interface NamespacedK8sApi<T extends K8sResource> {
	
	Future<KubeList<T>> list();
	Future<T> get(String name);
	Future<Status> delete(String name);

}
