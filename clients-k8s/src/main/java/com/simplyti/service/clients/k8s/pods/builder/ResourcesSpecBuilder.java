package com.simplyti.service.clients.k8s.pods.builder;


public interface ResourcesSpecBuilder<T> {

	ResourcesSpecBuilder<T> memmory(String mem);
	ResourcesSpecBuilder<T> cpu(String cpu);
	ResourcesBuilder<T> build();

}
