package com.simplyti.server.http.api.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.ResponseTypedWithBodyApiContext;
import com.simplyti.server.http.api.futurehandler.ResponseTypedWithBodyFutureHandle;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ResponseTypeWithBodyApiOperation;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.matcher.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.concurrent.Future;

public class RequestBodyTypableResponseTypedApiBuilderImpl<T> implements RequestBodyTypableResponseTypedApiBuilder<T> {

	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory responseTypedWithBodyContextFactory;
	private final ApiContextFactory requestResponseTypedContextFactory;
	
	private Map<String, Object> metadata;
	private boolean notFoundOnNull;
	private int maxBodyLength;

	public RequestBodyTypableResponseTypedApiBuilderImpl(ApiContextFactory responseTypedWithBodyContextFactory,ApiContextFactory requestResponseTypedContextFactory, ApiOperations operations, HttpMethod method, String path,
			Map<String,Object> metadata, boolean notFoundOnNull, int maxBodyLength) {
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.metadata=metadata;
		this.notFoundOnNull=notFoundOnNull;
		this.maxBodyLength=maxBodyLength;
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
	public <U> RequestResponseBodyTypedFinishableApiBuilder<U, T> withRequestBodyType(TypeLiteral<U> clazz) {
		return withRequestType(clazz);
	}

	@Override
	public <U> RequestResponseBodyTypedFinishableApiBuilder<U, T> withRequestType(TypeLiteral<U> clazz) {
		return new RequestResponseBodyTypedFinishableApiBuilderImpl<>(requestResponseTypedContextFactory,operations,method,path,metadata,notFoundOnNull,clazz);
	}

	@Override
	public void then(ResponseTypedWithRequestApiContextConsumer<T> consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new ResponseTypeWithBodyApiOperation<>(method,apiPattern,metadata,consumer,responseTypedWithBodyContextFactory, notFoundOnNull, maxBodyLength));
	}
	
	@Override
	public void thenFuture(Function<ResponseTypedWithBodyApiContext<T>, Future<T>> object) {
		then(new ResponseTypedWithBodyFutureHandle<>(object));
	}

	@Override
	public RequestBodyTypableResponseTypedApiBuilder<T> withNotFoundOnNull() {
		this.notFoundOnNull=true;
		return this;
	}

	@Override
	public RequestBodyTypableResponseTypedApiBuilder<T> withMaximunBodyLength(int maxBodyLength) {
		this.maxBodyLength=maxBodyLength;
		return this;
	}

	@Override
	public RequestBodyTypableResponseTypedApiBuilder<T> withMeta(String key, String value) {
		if(metadata==null) {
			metadata = new HashMap<>();
		}
		metadata.put(key, value);
		return this;
	}

}
