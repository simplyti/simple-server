package com.simplyti.service.clients.k8s.pods.builder;


public interface ContainerBuilder<T> {
	
	ContainerBuilder<T> withName(String name);

	ContainerBuilder<T> withImage(String string);
	
	ContainerBuilder<T> withCommand(String... command);
	
	EnvironmentBuilder<T> withEnvironment();

	ReadinessProbeBuilder<T> withReadinessProbe();

	ResourcesBuilder<T> withResources();

	LifecycleBuilder<T> withLifecycle();
	
	VolumeMountBuilder<T> withVolumeMount(String name);
	
	T build();

}