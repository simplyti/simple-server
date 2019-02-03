package com.simplyti.service.clients.k8s.common.list;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ListMetadata {
	
	private final String resourceVersion;
	
	@JsonCreator
	public ListMetadata(
			@JsonProperty("resourceVersion") String resourceVersion){
		this.resourceVersion=resourceVersion;
	}

}
