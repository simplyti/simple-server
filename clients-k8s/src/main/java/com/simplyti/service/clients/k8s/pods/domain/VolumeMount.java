package com.simplyti.service.clients.k8s.pods.domain;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class VolumeMount {

	private final String name;
	private final String mountPath;

	public VolumeMount(String name, String mountPath) {
		this.name=name;
		this.mountPath=mountPath;
	}

}
