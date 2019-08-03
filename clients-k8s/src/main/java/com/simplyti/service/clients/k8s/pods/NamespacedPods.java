package com.simplyti.service.clients.k8s.pods;

import com.simplyti.service.clients.k8s.common.NamespacedK8sApi;
import com.simplyti.service.clients.k8s.pods.builder.PodBuilder;
import com.simplyti.service.clients.k8s.pods.domain.Pod;

public interface NamespacedPods extends NamespacedK8sApi<Pod> {

	PodBuilder builder();
	
}
