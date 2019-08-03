package com.simplyti.service.clients.k8s.pods.builder;

public interface ResourcesBuilder {

	ResourcesSpecBuilder request();
	ResourcesSpecBuilder limit();
	ContainerBuilder<?> build();

}
