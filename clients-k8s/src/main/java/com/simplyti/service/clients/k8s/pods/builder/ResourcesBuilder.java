package com.simplyti.service.clients.k8s.pods.builder;

public interface ResourcesBuilder<T> {

	ResourcesSpecBuilder<T> request();
	ResourcesSpecBuilder<T> limit();
	ContainerBuilder<T> build();

}
