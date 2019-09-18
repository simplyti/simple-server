package com.simplyti.service.clients.k8s.pods.builder;

public interface VolumeMountBuilder<T> {

	VolumeMountBuilder<T> mountPath(String string);

	ContainerBuilder<T> build();

}
