package com.simplyti.service.matcher.jaxrs;

import com.fasterxml.classmate.ResolvedType;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public abstract class NamedDefaultedRestParam extends NamedRestParam{
	
	private final Object defaultValue;

	public NamedDefaultedRestParam(ResolvedType type, String name, Object defaultValue) {
		super(type, name);
		this.defaultValue=defaultValue;
	}

}
