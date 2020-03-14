package com.simplyti.server.http.api.operations;

import java.util.Set;

import javax.inject.Inject;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.pattern.ApiMatcher;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

public class ApiOperationResolverImpl implements ApiOperationResolver {
	
	private final ApiOperations operations;

	@Inject
	public ApiOperationResolverImpl(ApiOperations operations, Set<ApiProvider> apis, ApiBuilder builder) {
		this.operations=operations;
		apis.forEach(api->api.build(builder));
		this.operations.sort();
	}

	@Override
	public ApiMatchRequest resolveOperation(HttpMethod method, String path, QueryStringDecoder queryDecoder) {
		for(ApiOperation<? extends ApiContext> operation:operations.getAll()) {
			if(!method.equals(operation.method())) {
				continue;
			}
			
			ApiMatcher matcher = operation.pattern().matcher(path);
			if(!matcher.matches()) {
				continue;
			}
			
			return new ApiMatchRequest(operation,queryDecoder.parameters(),matcher);
		}
		return null;
	}

}
