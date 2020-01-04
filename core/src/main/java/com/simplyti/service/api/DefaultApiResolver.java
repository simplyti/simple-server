package com.simplyti.service.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.re2j.Matcher;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.channel.handler.ApiInvocationHandler;
import com.simplyti.service.channel.handler.ApiResponseEncoder;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

public class DefaultApiResolver implements ApiResolver {
	
	private final Collection<ApiOperation<?,?,?>> operations;
	
	private final ApiResponseEncoder apiResponseEncoder;
	private final ApiInvocationHandler apiInvocationHandler;
	
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final ServerHeadersHandler serverHeadersHandler;
	
	@Inject
	public DefaultApiResolver(Set<ApiProvider> apis, ApiBuilder builder, ApiResponseEncoder apiResponseEncoder,
			ApiInvocationHandler apiInvocationHandler, ExceptionHandler exceptionHandler,
			SyncTaskSubmitter syncTaskSubmitter, ServerHeadersHandler serverHeadersHandler){
		apis.forEach(api->api.build(builder));
		List<ApiOperation<?, ?,?>> builderOperations = builder.get();
		Collections.sort(builderOperations,RestOperationComparator.INSTANCE);
		this.operations=Collections.unmodifiableCollection(builderOperations);
		this.exceptionHandler=exceptionHandler;
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.serverHeadersHandler=serverHeadersHandler;
		this.apiResponseEncoder=apiResponseEncoder;
		this.apiInvocationHandler=apiInvocationHandler;
	}

	@Override
	public ApiRequestInitializer getOperationFor(HttpMethod method, QueryStringDecoder queryStringDecoder) {
		for(ApiOperation<?, ?, ?> operation:operations) {
			if(!method.equals(operation.method())) {
				continue;
			}
			
			Matcher matcher = operation.pathTemplate().matcher(queryStringDecoder.path());
			if(!matcher.matches()) {
				continue;
			}
			
			return initializer(new ApiMacher(operation, matcher, queryStringDecoder.parameters()));
		}
		return ApiRequestInitializer.none();
	}

	private ApiRequestInitializer initializer(ApiMacher matcher) {
		return new DefaultApiRequestInitializer(matcher,apiResponseEncoder, apiInvocationHandler,exceptionHandler,syncTaskSubmitter,serverHeadersHandler);
	}

	@Override
	public Collection<ApiOperation<?,?,?>> operations() {
		return operations;
	}

}
