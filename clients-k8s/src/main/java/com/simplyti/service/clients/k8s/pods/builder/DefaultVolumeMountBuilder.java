package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.VolumeMount;

public class DefaultVolumeMountBuilder<T> implements VolumeMountBuilder<T> {

	private final ContainerBuilder<T> parent;
	private final VolumeMountHolder holder;
	private final String name;
	
	private String mountPath;

	public DefaultVolumeMountBuilder(ContainerBuilder<T> parent, VolumeMountHolder holder, String name) {
		this.parent=parent;
		this.holder=holder;
		this.name=name;
	}

	@Override
	public VolumeMountBuilder<T> mountPath(String mountPath) {
		this.mountPath=mountPath;
		return this;
	}

	@Override
	public ContainerBuilder<T> build() {
		this.holder.addVolumeMount(new VolumeMount(name,mountPath));
		return parent;
	}

}
