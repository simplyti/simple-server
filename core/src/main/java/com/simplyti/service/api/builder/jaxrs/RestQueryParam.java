package com.simplyti.service.api.builder.jaxrs;


import com.fasterxml.classmate.ResolvedType;

public class RestQueryParam extends NamedDefaultedRestParam {

	public RestQueryParam(ResolvedType type, String name, Object defualtValue) {
		super(type,name,defualtValue);
	}

}
