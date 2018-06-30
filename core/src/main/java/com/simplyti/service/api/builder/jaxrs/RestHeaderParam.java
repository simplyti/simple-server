package com.simplyti.service.api.builder.jaxrs;

import com.fasterxml.classmate.ResolvedType;

public class RestHeaderParam extends NamedDefaultedRestParam {

	public RestHeaderParam(ResolvedType type, String name, Object defualtValue) {
		super(type,name,defualtValue);
	}
	
}
