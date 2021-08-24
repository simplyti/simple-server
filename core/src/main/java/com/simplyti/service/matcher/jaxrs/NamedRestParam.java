package com.simplyti.service.matcher.jaxrs;

import com.fasterxml.classmate.ResolvedType;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public abstract class NamedRestParam extends RestParam {

	private final String name;

	public NamedRestParam(ResolvedType type, String name) {
		super(type);
		this.name = name;
	}
	
}
