package com.simplyti.service.clients.k8s.pods.builder;

public class DefaultReadinessProbeBuilder<T> implements ReadinessProbeBuilder<T> {

	private final ContainerBuilder<T> parent;
	private final ReadinessProbeHolder holder;

	public DefaultReadinessProbeBuilder(ContainerBuilder<T> parent,
			ReadinessProbeHolder holder) {
		this.parent=parent;
		this.holder=holder;
	}

	@Override
	public HttpReadinessProbeBuilder<T> http() {
		return new DefaultHttpReadinessProbeBuilder<>(parent,holder);
	}

}
