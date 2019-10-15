package com.simplyti.service.clients.k8s.common.updater;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.json.coder.AnyConverter;
import com.dslplatform.json.JsonAttribute;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class JsonPatch {
	
	private final String op;
	private final String path;
	private final Object value;
	
	@CompiledJson
	public JsonPatch(
			String op,
			String path,
			@JsonAttribute(converter=AnyConverter.class) Object value) {
		this.op=op;
		this.path=path;
		this.value=value;
	}
	
	public JsonPatch(
			String op,
			String path) {
		this(op,path,null);
	}
	
	public static JsonPatch replace(String path, Object value) {
		return new JsonPatch("replace",path,value);
	}
	
	public static JsonPatch add(String path, Object value) {
		return new JsonPatch("add",path,value);
	}

	public static JsonPatch remove(String path) {
		return new JsonPatch("remove",path);
	}

}
