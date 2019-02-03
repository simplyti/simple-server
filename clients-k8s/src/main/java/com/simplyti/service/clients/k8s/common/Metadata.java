package com.simplyti.service.clients.k8s.common;

import java.time.LocalDateTime;
import java.util.Map;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;
import com.simplyti.service.clients.k8s.json.coder.LocalDateTimeDecoder;

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
	
	@JsonCreator
	public Metadata(
			@JsonProperty("name") String name,
			@JsonProperty("generateName") String generateName,
			@JsonProperty("namespace") String namespace,
			@JsonProperty("selfLink") String selfLink,
			@JsonProperty("uid") String uid,
			@JsonProperty("resourceVersion") String resourceVersion,
			@JsonProperty(value="creationTimestamp",decoder=LocalDateTimeDecoder.class) LocalDateTime creationTimestamp,
			@JsonProperty("labels") Map<String,String> labels,
			@JsonProperty("annotations")  Map<String,String> annotations){
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
