package com.simplyti.service.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.handler.codec.http.HttpMethod;

public class ApiResolver {
	
	private final Collection<ApiOperation<?,?>> operations;
	
	@Inject
	public ApiResolver(Set<ApiProvider> apis, ApiBuilder builder){
		apis.forEach(api->api.build(builder));
		List<ApiOperation<?, ?>> builderOperations = builder.get();
		Collections.sort(builderOperations,RestOperationComparator.INSTANCE);
		this.operations=Collections.unmodifiableCollection(builderOperations);
	}

	public Optional<ApiMacher> getOperationFor(HttpMethod method, String uri) {
		return operations.stream().filter(operation -> operation.method().equals(method))
				.map(operation -> new ApiMacher(operation, operation.pathTemplate().matcher(uri)))
				.filter(operation -> operation.matcher().matches())
				.findFirst();
	}

}
