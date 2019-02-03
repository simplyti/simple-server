package com.simplyti.service.clients.k8s.common.list;

import java.util.List;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class KubeList<T> {

	private final ListMetadata metadata;
	private final List<T> items;
	
	@JsonCreator
	public KubeList(
			@JsonProperty("metadata") ListMetadata metadata,
			@JsonProperty("items")  List<T> items){
		this.metadata=metadata;
		this.items=items;
	}
	
}
