package com.simplyti.service.clients.k8s.common.watch.domain;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;
import com.simplyti.service.clients.k8s.common.K8sResource;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class Event<T extends K8sResource> {
	
	private final EventType type;
	private final T object;
	
	@JsonCreator
	public Event(
			@JsonProperty("type") EventType type,
			@JsonProperty("object") T object) {
		this.type=type;
		this.object=object;
	}

}