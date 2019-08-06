package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.EnvironmentVariable;

public class DefaultEnvironmentBuilder<T> implements EnvironmentBuilder<T> {

	private final ContainerBuilder<T> parent;
	private final ContainerEnvironmentHolder holder;
	
	private String name;
	
	public DefaultEnvironmentBuilder(ContainerBuilder<T> parent, ContainerEnvironmentHolder holder) {
		this.parent=parent;
		this.holder=holder;
	}

	@Override
	public EnvironmentBuilder<T> name(String name) {
		this.name=name;
		return this;
	}

	@Override
	public ContainerBuilder<T> value(String value) {
		holder.setEnvironment(new EnvironmentVariable(name, value));
		return parent;
	}

}
