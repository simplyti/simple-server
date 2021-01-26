package com.simplyti.service.matcher.jaxrs;

import com.fasterxml.classmate.ResolvedType;

public class RestPathParam extends NamedRestParam {

	public RestPathParam(ResolvedType type, String name) {
		super(type,name);
	}
	
}
