package com.simplyti.service.clients.k8s.pods.builder;

public interface HttpReadinessProbeBuilder {

	HttpReadinessProbeBuilder path(String path);

	HttpReadinessProbeBuilder port(Object port);

	ContainerBuilder<?> build();

}
