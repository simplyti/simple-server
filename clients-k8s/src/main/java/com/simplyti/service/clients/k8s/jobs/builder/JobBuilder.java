package com.simplyti.service.clients.k8s.jobs.builder;

import com.simplyti.service.clients.k8s.common.builder.K8sResourceBuilder;
import com.simplyti.service.clients.k8s.jobs.domain.Job;
import com.simplyti.service.clients.k8s.pods.builder.ContainerBuilder;

public interface JobBuilder extends K8sResourceBuilder<JobBuilder,Job> {

	ContainerBuilder<JobBuilder> withContainer();

}
