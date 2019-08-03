package com.simplyti.service.clients.k8s.common;

import java.time.LocalDateTime;
import java.util.Map;

import com.dslplatform.json.CompiledJson;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@Builder
public class Metadata {
	
	private final String name;
	private final String generateName;
	private final String namespace;
	private final String selfLink;
	private final String uid;
	private final String resourceVersion;
	private final LocalDateTime creationTimestamp;
	private final Map<String,String> labels;
	private final Map<String,String> annotations;
	
	@CompiledJson
	public Metadata(
			String name,
			String generateName,
			String namespace,
			String selfLink,
			String uid,
			String resourceVersion,
			LocalDateTime creationTimestamp,
			Map<String,String> labels,
			Map<String,String> annotations){
		this.name=name;
		this.generateName=generateName;
		this.namespace=namespace;
		this.selfLink=selfLink;
		this.uid=uid;
		this.resourceVersion=resourceVersion;
		this.creationTimestamp=creationTimestamp;
		this.labels=labels;
		this.annotations=annotations;
	}

}
