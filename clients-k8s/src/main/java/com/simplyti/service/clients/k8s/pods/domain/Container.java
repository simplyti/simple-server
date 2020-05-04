package com.simplyti.service.clients.k8s.pods.domain;

import java.util.List;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Container {

	private final String name;
	private final String image;
	private final ImagePullPolicy imagePullPolicy;
	private final List<EnvironmentVariable> env;
	private final String[] command;
	private final Probe readinessProbe;
	private final Resources resources;
	private final Lifecycle lifecycle;
	private final List<VolumeMount> volumeMounts;

	@CompiledJson
	public Container(
			String name,
			String image,
			ImagePullPolicy imagePullPolicy,
			List<EnvironmentVariable> env,
			String[] command,
			Probe readinessProbe,
			Resources resources,
			Lifecycle lifecycle,
			List<VolumeMount> volumeMounts) {
		this.name=name;
		this.command=command;
		this.env=env;
		this.image=image;
		this.imagePullPolicy=imagePullPolicy;
		this.readinessProbe=readinessProbe;
		this.resources=resources;
		this.lifecycle = lifecycle;
		this.volumeMounts=volumeMounts;
	}
	
}
