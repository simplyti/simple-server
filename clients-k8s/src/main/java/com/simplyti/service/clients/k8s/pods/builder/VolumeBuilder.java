package com.simplyti.service.clients.k8s.pods.builder;

public interface VolumeBuilder {

	EmptyDirVolumeBuilder emptyDir(String name);

	ConfigMapVolumeBuilder configMap(String name);

}
