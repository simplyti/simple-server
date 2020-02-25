package com.simplyti.server.http.api.builder;

import javax.inject.Inject;

import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.service.api.serializer.json.Json;

import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class ApiBuilderImpl implements ApiBuilder {
	
	private final ApiOperations operations;
	private final Json json;
	
	@Override
	public HttpMethodApiBuilder when() {
		return new HttpMethodApiBuilderImpl(operations,json);
	}

}
