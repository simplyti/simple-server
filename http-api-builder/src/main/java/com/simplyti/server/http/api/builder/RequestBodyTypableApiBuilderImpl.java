package com.simplyti.server.http.api.builder;

import java.util.HashMap;
import java.util.Map;

import com.simplyti.server.http.api.builder.fileupload.FileUploadApiBuilder;
import com.simplyti.server.http.api.builder.fileupload.FileUploadApiBuilderImpl;
import com.simplyti.server.http.api.builder.stream.StreamedRequestResponseTypableApiBuilder;
import com.simplyti.server.http.api.builder.stream.StreamedRequestResponseTypableApiBuilderImpl;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.AnyWithBodyApiOperation;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.pattern.ApiPattern;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class RequestBodyTypableApiBuilderImpl implements RequestTypableApiBuilder {
	
	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory anyContextFactory;
	private final ApiContextFactory requestTypedContextFactory;
	private final ApiContextFactory responseTypedWithBodyContextFactory;
	private final ApiContextFactory requestResponseTypedContextFactory;
	private final ApiContextFactory streamAnyContextFactory;
	private final ApiContextFactory fileUploadContextFactory;
	
	private Map<String,Object> metadata;
	private boolean notFoundOnNull;
	private int maxBodyLength;

	public RequestBodyTypableApiBuilderImpl(ApiContextFactory anyContextFactory, ApiContextFactory requestTypedContextFactory,
			ApiContextFactory requestResponseTypedContextFactory, ApiContextFactory responseTypedWithBodyContextFactory,
			ApiContextFactory streamAnyContextFactory, ApiContextFactory fileUploadContextFactory,
			ApiOperations operations, HttpMethod method, String path) {
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.anyContextFactory=anyContextFactory;
		this.requestTypedContextFactory=requestTypedContextFactory;
		this.responseTypedWithBodyContextFactory=responseTypedWithBodyContextFactory;
		this.requestResponseTypedContextFactory=requestResponseTypedContextFactory;
		this.streamAnyContextFactory=streamAnyContextFactory;
		this.fileUploadContextFactory=fileUploadContextFactory;
	}
	
	@Override
	public <T> RequestBodyTypedFinishableApiBuilder<T> withRequestBodyType(TypeLiteral<T> type) {
		return new RequestBodyTypedFinishableApiBuilderImpl<>(operations,method,path,metadata,notFoundOnNull,type,requestTypedContextFactory,requestResponseTypedContextFactory);
	}

	@Override
	public <T> RequestBodyTypedFinishableApiBuilder<T> withRequestType(Class<T> clazz) {
		return withRequestBodyType(TypeLiteral.create(clazz));
	}
	
	@Override
	public <T> RequestBodyTypedFinishableApiBuilder<T> withRequestBodyType(Class<T> clazz) {
		return withRequestType(clazz);
	}

	@Override
	public <T> RequestBodyTypableResponseTypedApiBuilder<T> withResponseType(Class<T> clazz) {
		return new RequestBodyTypableResponseTypedApiBuilderImpl<>(responseTypedWithBodyContextFactory,requestResponseTypedContextFactory,operations,method,path,metadata,notFoundOnNull);
	}
	
	@Override
	public <T> RequestBodyTypableResponseTypedApiBuilder<T> withResponseBodyType(Class<T> clazz) {
		return withResponseType(clazz);
	}
	
	@Override
	public void then(ApiWithBodyContextConsumer consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new AnyWithBodyApiOperation(method,apiPattern,metadata,consumer,anyContextFactory,notFoundOnNull,maxBodyLength));
	}

	@Override
	public RequestTypableApiBuilder withMaximunBodyLength(int maxBodyLength) {
		this.maxBodyLength=maxBodyLength;
		return this;
	}
	
	@Override
	public RequestTypableApiBuilder withMeta(String key, String value) {
		if(metadata==null) {
			metadata = new HashMap<>();
		}
		metadata.put(key, value);
		return this;
	}
	
	@Override
	public RequestTypableApiBuilder withNotFoundOnNull() {
		this.notFoundOnNull=true;
		return this;
	}

	@Override
	public StreamedRequestResponseTypableApiBuilder withStreamedInput() {
		return new StreamedRequestResponseTypableApiBuilderImpl(streamAnyContextFactory,operations,method,path);
	}

	@Override
	public FileUploadApiBuilder asFileUpload() {
		return new FileUploadApiBuilderImpl(fileUploadContextFactory,operations,method,path);
	}

}
