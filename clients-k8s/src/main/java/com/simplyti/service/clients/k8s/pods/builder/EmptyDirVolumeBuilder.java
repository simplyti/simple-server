package com.simplyti.service.clients.k8s.pods.builder;

public interface EmptyDirVolumeBuilder {

	EmptyDirVolumeBuilder medium(String medium);

	PodBuilder build();

}
