package com.simplyti.service.clients.k8s.ingresses;

import com.simplyti.service.clients.k8s.common.NamespacedK8sApi;
import com.simplyti.service.clients.k8s.ingresses.builder.IngressBuilder;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;
import com.simplyti.service.clients.k8s.ingresses.updater.IngressUpdater;

public interface NamespacedIngresses extends NamespacedK8sApi<Ingress> {

	IngressBuilder builder();

	IngressUpdater update(String name);


}
