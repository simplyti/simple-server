package com.simplyti.service.clients.k8s.pods;

import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.Namespaced;
import com.simplyti.service.clients.k8s.pods.domain.Pod;

public interface Pods extends Namespaced<Pod,NamespacedPods>, K8sApi<Pod>{

}
