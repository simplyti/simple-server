package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.Resources;
import com.simplyti.service.clients.k8s.pods.domain.ResourcesSpec;

public class DefaultResourcesBuilder<T> implements ResourcesBuilder<T> {

	private final ContainerBuilder<T> parent;
	private final ResourcesHolder holder;
	
	private ResourcesSpec request;
	private ResourcesSpec limit;

	public DefaultResourcesBuilder(ContainerBuilder<T> parent,
			ResourcesHolder holder) {
		this.parent=parent;
		this.holder=holder;
	}

	@Override
	public ResourcesSpecBuilder<T> request() {
		return new DefaultResourcesSpecBuilder<>(this,spec->this.request=spec);
	}

	@Override
	public ResourcesSpecBuilder<T> limit() {
		return new DefaultResourcesSpecBuilder<>(this,spec->this.limit=spec);
	}

	@Override
	public ContainerBuilder<T> build() {
		holder.setResources(new Resources(limit,request));
		return parent;
	}

}
