package com.simplyti.service.clients.k8s;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public enum K8sAPI {
	
	V1("api","v1"),
	BATCH1("apis","batch/v1"),
	BETA1("apis","extensions/v1beta1");
	
	private final String api;
	private final String version;
	private final String path;
	
	private K8sAPI(String api, String version) {
		this.api=api;
		this.version=version;
		this.path=String.format("/%s/%s", api,version);
	}
	
	public Object path() {
		return path;
	}

}
