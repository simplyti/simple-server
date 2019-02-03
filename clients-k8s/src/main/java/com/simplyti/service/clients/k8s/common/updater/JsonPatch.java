package com.simplyti.service.clients.k8s.common.updater;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class JsonPatch {
	
	private final String op;
	private final String path;
	private final Object value;
	
	public static JsonPatch replace(String path, Object value) {
		return new JsonPatch("replace",path,value);
	}
	
	public static JsonPatch add(String path, Object value) {
		return new JsonPatch("add",path,value);
	}

}
