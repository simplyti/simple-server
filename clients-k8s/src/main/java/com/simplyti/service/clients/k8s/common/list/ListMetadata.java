package com.simplyti.service.clients.k8s.common.list;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ListMetadata {
	
	private final String resourceVersion;
	
	@CompiledJson
	public ListMetadata(String resourceVersion){
		this.resourceVersion=resourceVersion;
	}

}
