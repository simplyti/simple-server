package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.common.builder.K8sResourceBuilder;
import com.simplyti.service.clients.k8s.pods.domain.Pod;
import com.simplyti.service.clients.k8s.pods.domain.RestartPolicy;

public interface PodBuilder extends K8sResourceBuilder<PodBuilder,Pod> {

	ContainerBuilder<PodBuilder> withContainer();

	DefaultPodBuilder withImagePullSecret(String name);

	PodBuilder withRestartPolicy(RestartPolicy never);

	PodBuilder withTerminationGracePeriodSeconds(int terminationGraceSeconds);

}
