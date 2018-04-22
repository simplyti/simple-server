package com.simplyti.service.api.builder.jaxrs;

import com.fasterxml.classmate.ResolvedType;

public class RestPathParam extends RestParam {

	private final String name;

	public RestPathParam(ResolvedType type, String name) {
		super(type);
		this.name = name;
	}
	
	public  String name() {
		return name;
	}

}
