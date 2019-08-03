package com.simplyti.service.clients.k8s.pods.builder;


public interface ContainerBuilder<T> {
	
	ContainerBuilder<T> withName(String name);

	ContainerBuilder<T> withImage(String string);
	
	ContainerBuilder<T> withCommand(String... command);

	ReadinessProbeBuilder withReadinessProbe();

	ResourcesBuilder withResources();
	
	T build();

}
