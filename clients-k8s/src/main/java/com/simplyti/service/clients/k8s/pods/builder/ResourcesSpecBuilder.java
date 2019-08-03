package com.simplyti.service.clients.k8s.pods.builder;


public interface ResourcesSpecBuilder {

	ResourcesSpecBuilder memmory(String mem);
	ResourcesSpecBuilder cpu(String cpu);
	ResourcesBuilder build();

}
