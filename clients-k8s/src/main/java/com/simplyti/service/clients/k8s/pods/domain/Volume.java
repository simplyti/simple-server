package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Volume {
	
	private final String name;
	private final EmptyDirVolume emptyDir;
	private final ConfigMapVolume configMap;
	
	@CompiledJson
	public Volume(String name, EmptyDirVolume emptyDir, ConfigMapVolume configMap) {
		this.name = name;
		this.emptyDir = emptyDir;
		this.configMap = configMap;
	}

	public Volume(String name, EmptyDirVolume emptyDir) {
		this(name, emptyDir, null);
	}

	public Volume(String name, ConfigMapVolume configMap) {
		this(name, null, configMap);
	}


}
