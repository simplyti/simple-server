package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.ResourcesSpec;

public class DefaultResourcesSpecBuilder<T> implements ResourcesSpecBuilder<T> {

	private final ResourcesBuilder<T> parent;
	private final ResourceSpecHolder holder;
	
	private String mem;
	private String cpu;

	public DefaultResourcesSpecBuilder(ResourcesBuilder<T> parent,ResourceSpecHolder holder) {
		this.parent=parent;
		this.holder=holder;
	}

	@Override
	public ResourcesSpecBuilder<T> memmory(String mem) {
		this.mem=mem;
		return this;
	}

	@Override
	public ResourcesSpecBuilder<T> cpu(String cpu) {
		this.cpu=cpu;
		return this;
	}

	@Override
	public ResourcesBuilder<T> build() {
		holder.set(new ResourcesSpec(mem,cpu));
		return parent;
	}

}
