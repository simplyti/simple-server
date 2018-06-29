package com.simplyti.service.api.builder.jaxrs;

import com.fasterxml.classmate.ResolvedType;

public class RestHeaderParam extends RestParam {

	private final String name;
	private final Object defualtValue;

	public RestHeaderParam(ResolvedType type, String name, Object defualtValue) {
		super(type);
		this.name = name;
		this.defualtValue=defualtValue;
	}
	
	public  Object defaultValue() {
		return defualtValue;
	}
	
	public  String name() {
		return name;
	}

}
