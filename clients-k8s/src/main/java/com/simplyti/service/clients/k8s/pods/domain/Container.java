package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Container {

	private final String name;
	private final String image;
	private final String[] command;
	private final Probe readinessProbe;
	private final Resources resources;

	@CompiledJson
	public Container(
			String name,
			String image,
			String[] command,
			Probe readinessProbe,
			Resources resources) {
		this.name=name;
		this.command=command;
		this.image=image;
		this.readinessProbe=readinessProbe;
		this.resources=resources;
	}
	
}
