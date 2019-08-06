package com.simplyti.service.clients.k8s.pods.builder;

public interface EnvironmentBuilder<T> {

	EnvironmentBuilder<T> name(String name);

	ContainerBuilder<T> value(String value);

}
