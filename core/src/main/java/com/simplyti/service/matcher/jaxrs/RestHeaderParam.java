package com.simplyti.service.matcher.jaxrs;

import com.fasterxml.classmate.ResolvedType;

public class RestHeaderParam extends NamedDefaultedRestParam {

	public RestHeaderParam(ResolvedType type, String name, Object defualtValue) {
		super(type,name,defualtValue);
	}
	
}
