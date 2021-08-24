package com.simplyti.server.http.api.builder;

import javax.inject.Inject;

import com.simplyti.server.http.api.builder.jaxrs.JaxRSBuilder;
import com.simplyti.server.http.api.builder.jaxrs.JaxRsApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.matcher.di.InstanceProvider;
import com.simplyti.service.sync.SyncTaskSubmitter;

import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class ApiBuilderImpl implements ApiBuilder {
	
	private final ApiOperations operations;
	private final InstanceProvider instanceProvider;
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final JaxRsApiContextFactory jaxRsContextFactory;
	private final Json json;
	
	@Override
	public HttpMethodApiBuilder when() {
		return new HttpMethodApiBuilderImpl(operations,json);
	}

	@Override
	public void usingJaxRSContract(Class<?> clazz) {
		JaxRSBuilder.build(clazz, instanceProvider, syncTaskSubmitter, operations, jaxRsContextFactory);
	}
	
}
