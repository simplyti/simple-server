package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.ResourcesSpec;

public class DefaultResourcesSpecBuilder implements ResourcesSpecBuilder {

	private final ResourcesBuilder parent;
	private final ResourceSpecHolder holder;
	
	private String mem;
	private String cpu;

	public DefaultResourcesSpecBuilder(ResourcesBuilder parent,ResourceSpecHolder holder) {
		this.parent=parent;
		this.holder=holder;
	}

	@Override
	public ResourcesSpecBuilder memmory(String mem) {
		this.mem=mem;
		return this;
	}

	@Override
	public ResourcesSpecBuilder cpu(String cpu) {
		this.cpu=cpu;
		return this;
	}

	@Override
	public ResourcesBuilder build() {
		holder.set(new ResourcesSpec(mem,cpu));
		return parent;
	}

}
