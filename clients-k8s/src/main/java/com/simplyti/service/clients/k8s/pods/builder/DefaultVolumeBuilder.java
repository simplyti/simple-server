package com.simplyti.service.clients.k8s.pods.builder;

public class DefaultVolumeBuilder implements VolumeBuilder {

	private final PodBuilder parent;
	private final VolumeHolder holder;
	
	public DefaultVolumeBuilder(PodBuilder parent, VolumeHolder holder) {
		this.parent=parent;
		this.holder=holder;
	}

	@Override
	public EmptyDirVolumeBuilder emptyDir(String name) {
		return new DefaultEmptyDirVolumeBuilder(parent,holder,name);
	}


}
