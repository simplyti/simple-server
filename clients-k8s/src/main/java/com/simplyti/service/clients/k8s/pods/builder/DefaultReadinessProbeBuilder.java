package com.simplyti.service.clients.k8s.pods.builder;

public class DefaultReadinessProbeBuilder implements ReadinessProbeBuilder {

	private final ContainerBuilder<?> parent;
	private final ReadinessProbeHolder holder;

	public DefaultReadinessProbeBuilder(ContainerBuilder<?> parent,
			ReadinessProbeHolder holder) {
		this.parent=parent;
		this.holder=holder;
	}

	@Override
	public HttpReadinessProbeBuilder http() {
		return new DefaultHttpReadinessProbeBuilder(parent,holder);
	}

}
