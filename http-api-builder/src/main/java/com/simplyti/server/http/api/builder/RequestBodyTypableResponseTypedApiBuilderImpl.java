package com.simplyti.server.http.api.builder;

import java.util.Map;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ResponseTypeWithBodyApiOperation;
import com.simplyti.server.http.api.pattern.ApiPattern;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class RequestBodyTypableResponseTypedApiBuilderImpl<T> implements RequestBodyTypableResponseTypedApiBuilder<T> {

	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory responseTypedWithBodyContextFactory;
	private final ApiContextFactory requestResponseTypedContextFactory;
	
	private final Map<String, Object> metadata;
	private boolean notFoundOnNull;

	public RequestBodyTypableResponseTypedApiBuilderImpl(ApiContextFactory responseTypedWithBodyContextFactory,ApiContextFactory requestResponseTypedContextFactory, ApiOperations operations, HttpMethod method, String path,
			Map<String,Object> metadata, boolean notFoundOnNull) {
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.metadata=metadata;
		this.notFoundOnNull=notFoundOnNull;
		this.responseTypedWithBodyContextFactory=responseTypedWithBodyContextFactory;
		this.requestResponseTypedContextFactory=requestResponseTypedContextFactory;
	}
	
	@Override
	public <U> RequestResponseBodyTypedFinishableApiBuilder<U, T> withRequestType(Class<U> clazz) {
		return new RequestResponseBodyTypedFinishableApiBuilderImpl<>(requestResponseTypedContextFactory,operations,method,path,metadata,notFoundOnNull,TypeLiteral.create(clazz));
	}
	
	@Override
	public <U> RequestResponseBodyTypedFinishableApiBuilder<U, T> withRequestBodyType(Class<U> clazz) {
		return withRequestType(clazz);
	}

	@Override
	public void then(ResponseTypedWithRequestApiContextConsumer<T> consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new ResponseTypeWithBodyApiOperation<>(method,apiPattern,null,consumer,responseTypedWithBodyContextFactory, notFoundOnNull));
	}

	@Override
	public RequestBodyTypableResponseTypedApiBuilder<T> withNotFoundOnNull() {
		this.notFoundOnNull=true;
		return this;
	}

}
