package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.EmptyDirVolume;
import com.simplyti.service.clients.k8s.pods.domain.Volume;

public class DefaultEmptyDirVolumeBuilder implements EmptyDirVolumeBuilder {

	private final PodBuilder parent;
	private final VolumeHolder holder;
	private final String name;
	
	private String medium;

	public DefaultEmptyDirVolumeBuilder(PodBuilder parent, VolumeHolder holder, String name) {
		this.parent=parent;
		this.holder=holder;
		this.name=name;
	}

	@Override
	public EmptyDirVolumeBuilder medium(String medium) {
		this.medium=medium;
		return this;
	}

	@Override
	public PodBuilder build() {
		holder.addVolume(new Volume(name,new EmptyDirVolume(medium)));
		return parent;
	}

}
