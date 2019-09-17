package com.simplyti.service.clients.k8s.pods.builder;

public interface HttpReadinessProbeBuilder<T> {

	HttpReadinessProbeBuilder<T> path(String path);

	HttpReadinessProbeBuilder<T> port(Object port);

	ContainerBuilder<T> build();

}
