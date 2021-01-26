package com.simplyti.service.matcher.jaxrs;

import com.fasterxml.classmate.ResolvedType;

import lombok.Getter;

@Getter
public class RestBodyParam extends RestParam {

	public RestBodyParam(ResolvedType resolvedType) {
		super(resolvedType);
	}

}
