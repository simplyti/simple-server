package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.Resources;
import com.simplyti.service.clients.k8s.pods.domain.ResourcesSpec;

public class DefaultResourcesBuilder implements ResourcesBuilder {

	private final ContainerBuilder<?> parent;
	private final ResourcesHolder holder;
	
	private ResourcesSpec request;
	private ResourcesSpec limit;

	public DefaultResourcesBuilder(ContainerBuilder<?> parent,
			ResourcesHolder holder) {
		this.parent=parent;
		this.holder=holder;
	}

	@Override
	public ResourcesSpecBuilder request() {
		return new DefaultResourcesSpecBuilder(this,spec->this.request=spec);
	}

	@Override
	public ResourcesSpecBuilder limit() {
		return new DefaultResourcesSpecBuilder(this,spec->this.limit=spec);
	}

	@Override
	public ContainerBuilder<?> build() {
		holder.setResources(new Resources(limit,request));
		return parent;
	}

}
