package com.simplyti.server.http.api.builder;

import java.util.Map;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.RequestTypeApiOperation;
import com.simplyti.server.http.api.pattern.ApiPattern;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class RequestBodyTypedFinishableApiBuilderImpl<T> implements RequestBodyTypedFinishableApiBuilder<T> {
	
	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final TypeLiteral<T> requestType;
	private final ApiContextFactory requestTypedContextFactory;
	private final ApiContextFactory requestResponseTypedContextFactory;
	private final Map<String, Object> metadata;
	private final boolean notFoundOnNull;

	public RequestBodyTypedFinishableApiBuilderImpl(ApiOperations operations, HttpMethod method, String path, Map<String,Object> metadata, 
			boolean notFoundOnNull, TypeLiteral<T> requestType, 
			ApiContextFactory requestTypedContextFactory, ApiContextFactory requestResponseTypedContextFactory) {
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.metadata=metadata;
		this.notFoundOnNull=notFoundOnNull;
		this.requestType=requestType;
		this.requestTypedContextFactory=requestTypedContextFactory;
		this.requestResponseTypedContextFactory=requestResponseTypedContextFactory;
	}
	
	@Override
	public <U> RequestResponseBodyTypedFinishableApiBuilder<T, U> withResponseType(TypeLiteral<U> type) {
		return new RequestResponseBodyTypedFinishableApiBuilderImpl<>(requestResponseTypedContextFactory,operations,method,path,metadata,notFoundOnNull,requestType);
	}

	@Override
	public <U> RequestResponseBodyTypedFinishableApiBuilder<T, U> withResponseType(Class<U> clazz) {
		return withResponseType(TypeLiteral.create(clazz));
	}
	
	@Override
	public <U> RequestResponseBodyTypedFinishableApiBuilder<T, U> withResponseBodyType(Class<U> clazz) {
		return withResponseType(clazz);
	}

	@Override
	public void then(RequestTypedApiContextConsumer<T> consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new RequestTypeApiOperation<>(method,apiPattern,null,requestType,consumer,requestTypedContextFactory, notFoundOnNull));
	}

}
