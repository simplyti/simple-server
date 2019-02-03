package com.simplyti.service.clients.k8s.ingresses;

import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.Namespaced;
import com.simplyti.service.clients.k8s.ingresses.builder.IngressBuilder;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;

public interface Ingresses extends Namespaced<Ingress,NamespacedIngresses>, K8sApi<Ingress>{

	IngressBuilder builder(String namespace);

}
