package com.simplyti.service.clients.k8s.common.list;

import java.util.List;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class KubeList<T> {

	private final ListMetadata metadata;
	private final List<T> items;
	
	@CompiledJson
	public KubeList(
			ListMetadata metadata,
			List<T> items){
		this.metadata=metadata;
		this.items=items;
	}
	
}
