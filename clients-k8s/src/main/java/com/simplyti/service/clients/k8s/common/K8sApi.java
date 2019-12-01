package com.simplyti.service.clients.k8s.common;

import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.util.concurrent.Future;

public interface K8sApi<T extends K8sResource> {
	
	public Future<KubeList<T>> list();
	
	public Observable<T> watch(String resourceVersion);

}
