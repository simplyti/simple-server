package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.HttpProbe;
import com.simplyti.service.clients.k8s.pods.domain.Probe;

public class DefaultHttpReadinessProbeBuilder implements HttpReadinessProbeBuilder {

	private final ContainerBuilder<?> parent;
	private final ReadinessProbeHolder holder;
	
	private Object port;
	private String path;

	public DefaultHttpReadinessProbeBuilder(ContainerBuilder<?> parent, ReadinessProbeHolder holder) {
		this.parent=parent;
		this.holder=holder;
	}

	@Override
	public HttpReadinessProbeBuilder path(String path) {
		this.path=path;
		return this;
	}

	@Override
	public HttpReadinessProbeBuilder port(Object port) {
		this.port=port;
		return this;
	}

	@Override
	public ContainerBuilder<?> build() {
		holder.addReadinessProbe(new Probe(new HttpProbe(path,port)));
		return parent;
	}

}
