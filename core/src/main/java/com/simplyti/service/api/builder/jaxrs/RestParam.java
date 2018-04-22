package com.simplyti.service.api.builder.jaxrs;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Splitter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class RestParam {

	private final ResolvedType type;
	
	public static Object convert(String value, ResolvedType type) {
		if (type.isInstanceOf(Short.class) || type.isInstanceOf(Short.TYPE)) {
			return Short.parseShort(value);
		} else if (type.isInstanceOf(Integer.class) || type.isInstanceOf(Integer.TYPE)) {
			return Integer.parseInt(value);
		} else if (type.isInstanceOf(Long.class) || type.isInstanceOf(Long.TYPE)) {
			return Long.parseLong(value);
		} else if (type.isInstanceOf(Float.class) || type.isInstanceOf(Float.TYPE)) {
			return Float.parseFloat(value);
		} else if (type.isInstanceOf(Double.class) || type.isInstanceOf(Double.TYPE)) {
			return Double.parseDouble(value);
		} else if (type.isInstanceOf(List.class)) {
			ResolvedType itemType = type.typeParametersFor(List.class).get(0);
			return StreamSupport.stream(Splitter.on(',').split(value).spliterator(), false).map(item -> convert(item, itemType)).collect(Collectors.toList());
		} else {
			return value;
		}
	}

}
