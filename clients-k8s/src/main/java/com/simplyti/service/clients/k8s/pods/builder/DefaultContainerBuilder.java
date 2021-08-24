package com.simplyti.service.clients.k8s.pods.builder;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.k8s.pods.domain.*;

public class DefaultContainerBuilder<T> implements ContainerBuilder<T>, ReadinessProbeHolder,ResourcesHolder,ContainerEnvironmentHolder, LifecycleHolder, VolumeMountHolder {

	private final T parent;
	private final ContainerHolder containerHolder;
	
	private String name;
	private String image;
	private Probe readinessProbe;
	private Resources resources;
	private String[] command;
	private List<EnvironmentVariable> environments;
	private Lifecycle lifecycle;
	private List<VolumeMount> volumeMounts;
	private ImagePullPolicy imagePullPolicy;

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
	public ContainerBuilder<T> withImagePullPolicy(ImagePullPolicy policy) {
		this.imagePullPolicy=policy;
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
		containerHolder.addContainer(new Container(name,image,imagePullPolicy,environments,command,readinessProbe,resources, lifecycle, volumeMounts));
		return parent;
	}

	@Override
	public ReadinessProbeBuilder<T> withReadinessProbe() {
		return new DefaultReadinessProbeBuilder<>(this,this);
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
	public LifecycleBuilder<T> withLifecycle() {
		return new DefaultLifecycleBuilder<>(this, this);
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
	
	@Override
	public void addVolumeMount(VolumeMount volumeMount) {
		if(volumeMounts==null) {
			this.volumeMounts=new ArrayList<>();
		}
		volumeMounts.add(volumeMount);
	}

	@Override
	public void setLifecycle(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
	}

	@Override
	public VolumeMountBuilder<T> withVolumeMount(String name) {
		return new DefaultVolumeMountBuilder<T>(this,this,name);
	}

}
