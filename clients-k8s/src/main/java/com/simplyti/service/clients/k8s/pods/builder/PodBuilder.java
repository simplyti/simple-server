package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.common.builder.K8sResourceBuilder;
import com.simplyti.service.clients.k8s.pods.domain.Pod;

public interface PodBuilder extends K8sResourceBuilder<PodBuilder,Pod> {

	ContainerBuilder<PodBuilder> withContainer();

	DefaultPodBuilder withImagePullSecret(String name);

}
