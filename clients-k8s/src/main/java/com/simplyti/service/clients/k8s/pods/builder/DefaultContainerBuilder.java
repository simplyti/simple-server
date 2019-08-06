package com.simplyti.service.clients.k8s.pods.builder;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.k8s.pods.domain.Container;
import com.simplyti.service.clients.k8s.pods.domain.EnvironmentVariable;
import com.simplyti.service.clients.k8s.pods.domain.Probe;
import com.simplyti.service.clients.k8s.pods.domain.Resources;

public class DefaultContainerBuilder<T> implements ContainerBuilder<T>, ReadinessProbeHolder,ResourcesHolder,ContainerEnvironmentHolder {

	private final T parent;
	private final ContainerHolder containerHolder;
	
	private String name;
	private String image;
	private Probe readinessProbe;
	private Resources resources;
	private String[] command;
	private List<EnvironmentVariable> environments;

	public DefaultContainerBuilder(T parent, ContainerHolder containerHolder) {
		this.parent=parent;
		this.containerHolder=containerHolder;
	}

	@Override
	public ContainerBuilder<T> withImage(String image) {
		this.image=image;
		return this;
	}

	@Override
	public ContainerBuilder<T> withName(String name) {
		this.name=name;
		return this;
	}
	
	@Override
	public ContainerBuilder<T> withCommand(String... command) {
		this.command=command;
		return this;
	}
	
	@Override
	public EnvironmentBuilder<T> withEnvironment() {
		return new DefaultEnvironmentBuilder<>(this,this);
	}

	@Override
	public T build() {
		containerHolder.addContainer(new Container(name,image,environments,command,readinessProbe,resources));
		return parent;
	}

	@Override
	public ReadinessProbeBuilder withReadinessProbe() {
		return new DefaultReadinessProbeBuilder(this,this);
	}

	@Override
	public void addReadinessProbe(Probe probe) {
		this.readinessProbe=probe;
	}

	@Override
	public ResourcesBuilder<T> withResources() {
		return new DefaultResourcesBuilder<>(this,this);
	}

	@Override
	public void setResources(Resources resources) {
		this.resources=resources;
	}

	@Override
	public void setEnvironment(EnvironmentVariable environment) {
		if(environments==null) {
			this.environments=new ArrayList<>();
		}
		environments.add(environment);
	}

}
